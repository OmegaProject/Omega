/*******************************************************************************
 * Copyright (C) 2014 University of Massachusetts Medical School
 * Alessandro Rigano (Program in Molecular Medicine)
 * Caterina Strambio De Castillia (Program in Molecular Medicine)
 *
 * Created by the Open Microscopy Environment inteGrated Analysis (OMEGA) team: 
 * Alex Rigano, Caterina Strambio De Castillia, Jasmine Clark, Vanni Galli, 
 * Raffaello Giulietti, Loris Grossi, Eric Hunter, Tiziano Leidi, Jeremy Luban, 
 * Ivo Sbalzarini and Mario Valle.
 *
 * Key contacts:
 * Caterina Strambio De Castillia: caterina.strambio@umassmed.edu
 * Alex Rigano: alex.rigano@umassmed.edu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package edu.umassmed.omega.omeroPlugin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import omero.RDouble;
import omero.ServerError;
import omero.client;
import omero.api.IAdminPrx;
import omero.api.IContainerPrx;
import omero.api.IQueryPrx;
import omero.api.RawPixelsStorePrx;
import omero.api.RenderingEnginePrx;
import omero.api.ServiceFactoryPrx;
import omero.api.ServiceInterfacePrx;
import omero.api.StatefulServiceInterfacePrx;
import omero.api.ThumbnailStorePrx;
import omero.model.Dataset;
import omero.model.Experimenter;
import omero.model.ExperimenterGroup;
import omero.model.IObject;
import omero.model.PlaneInfoI;
import omero.model.Project;
import omero.romio.PlaneDef;
import omero.sys.ParametersI;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.ProjectData;
import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import Ice.ConnectionRefusedException;
import Ice.DNSException;
import edu.umassmed.omega.core.OmegaLogFileManager;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaGateway;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaLoginCredentials;
import edu.umassmed.omega.dataNew.imageDBConnectionElements.OmegaServerInformation;

/**
 * Entry point to access the services. Code should be provided to keep those
 * services alive.
 */
public class OmeroGateway extends OmegaGateway {

	/**
	 * The maximum number of thumbnails retrieved before restarting the
	 * thumbnails service.
	 */
	private static final int MAX_RETRIEVAL = 100;

	/** Keeps the client's session alive. */
	private ScheduledThreadPoolExecutor executor;

	/**
	 * The Blitz client object, this is the entry point to the OMERO Server
	 * using a secure connection.
	 */
	private client secureClient;

	/**
	 * The entry point provided by the connection library to access the various
	 * <i>OMERO</i> services.
	 */
	private ServiceFactoryPrx entryEncrypted;

	/** Collection of services to keep alive. */
	private final List<ServiceInterfacePrx> services;

	/** Collection of services to keep alive. */
	private final Map<Long, StatefulServiceInterfacePrx> reServices;

	/** The container service. */
	private IContainerPrx containerService;

	/** The Admin service. */
	private IAdminPrx adminService;

	/** The thumbnail service. */
	private ThumbnailStorePrx thumbnailService;

	/**
	 * The number of thumbnails already retrieved. Resets to <code>0</code> when
	 * the value equals {@link #MAX_RETRIEVAL}.
	 */
	private int thumbnailRetrieval;

	/**
	 * Creates a <code>BufferedImage</code> from the passed array of bytes.
	 * 
	 * @param values
	 *            The array of bytes.
	 * @return See above.
	 * @throws RenderingServiceException
	 *             If we cannot create an image.
	 */
	private BufferedImage createImage(final byte[] values) {
		try {
			final ByteArrayInputStream stream = new ByteArrayInputStream(values);
			final BufferedImage image = ImageIO.read(stream);
			image.setAccelerationPriority(1f);
			return image;
		} catch (final Exception e) {
			// TODO ManageException
		}

		return null;
	}

	/**
	 * Returns the {@link ThumbnailStorePrx} service.
	 * 
	 * @return See above.
	 * @throws DSOutOfServiceException
	 *             If the connection is broken, or logged in
	 * @throws DSAccessException
	 *             If an error occurred while trying to retrieve data from OMERO
	 *             service.
	 */
	private ThumbnailStorePrx getThumbnailService() {
		try {
			if (this.thumbnailRetrieval == OmeroGateway.MAX_RETRIEVAL) {
				this.thumbnailRetrieval = 0;
				// to be on the save side
				if (this.thumbnailService != null) {
					this.thumbnailService.close();
				}
				this.services.remove(this.thumbnailService);
				this.thumbnailService = null;
			}
			if (this.thumbnailService == null) {
				this.thumbnailService = this.entryEncrypted
				        .createThumbnailStore();
				this.services.add(this.thumbnailService);
			}
			this.thumbnailRetrieval++;
			return this.thumbnailService;
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}

		// TODO Manage Null Case
		return null;
	}

	/**
	 * Returns the {@link IContainerPrx} service.
	 * 
	 * @return See above.
	 * @throws DSOutOfServiceException
	 *             If the connection is broken, or logged in
	 * @throws DSAccessException
	 *             If an error occurred while trying to retrieve data from OMERO
	 *             service.
	 */
	private IContainerPrx getContainerService() {
		try {
			if (this.containerService == null) {
				this.containerService = this.entryEncrypted
				        .getContainerService();
				this.services.add(this.containerService);
			}
			return this.containerService;
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}

		// TODO Manage Null Case
		return null;
	}

	/**
	 * Returns the {@link IAdminPrx} service.
	 * 
	 * @return See above.
	 */
	private IAdminPrx getAdminService() {
		try {
			if (this.adminService == null) {
				this.adminService = this.entryEncrypted.getAdminService();
				this.services.add(this.adminService);
			}
			return this.adminService;
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}

		// TODO Manage Null Case
		return null;
	}

	/**
	 * Returns the {@link RenderingEnginePrx Rendering service}.
	 * 
	 * @return See above.
	 * @throws DSOutOfServiceException
	 *             If the connection is broken, or logged in
	 * @throws DSAccessException
	 *             If an error occurred while trying to retrieve data from OMERO
	 *             service.
	 */
	private RenderingEnginePrx createRenderingService() {
		try {
			final RenderingEnginePrx engine = this.entryEncrypted
			        .createRenderingEngine();
			return engine;
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}

		// TODO Manage Null Case
		return null;
	}

	/** Creates a new instance. */
	public OmeroGateway() {
		super();
		this.services = new ArrayList<ServiceInterfacePrx>();
		this.reServices = new HashMap<Long, StatefulServiceInterfacePrx>();
	}

	/** Keeps the services alive. */
	protected void keepSessionAlive() {
		final int n = this.services.size() + this.reServices.size();
		final ServiceInterfacePrx[] entries = new ServiceInterfacePrx[n];
		final Iterator<ServiceInterfacePrx> i = this.services.iterator();
		int index = 0;
		while (i.hasNext()) {
			entries[index] = i.next();
			index++;
		}
		final Iterator<Long> j = this.reServices.keySet().iterator();
		while (j.hasNext()) {
			entries[index] = this.reServices.get(j.next());
			index++;
		}
		try {
			this.entryEncrypted.keepAllAlive(entries);
		} catch (final Exception e) {
			// Handle exception. Here
		}
	}

	/**
	 * Logs in. otherwise.
	 * 
	 * @param loginCred
	 *            Host the information to connect.
	 * @return <code>true</code> if connected, <code>false</code>
	 * @throws ServerError
	 * @throws PermissionDeniedException
	 * @throws CannotCreateSessionException
	 */
	@Override
	public int connect(final OmegaLoginCredentials loginCred,
	        final OmegaServerInformation serverInfo) {
		// TODO check with cases should throw exception and what shouldn't
		this.setConnected(false);
		this.secureClient = new client(serverInfo.getHostName(),
		        serverInfo.getPort());
		try {
			this.entryEncrypted = this.secureClient.createSession(
			        loginCred.getUserName(), loginCred.getPassword());
		} catch (final CannotCreateSessionException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return 1;
		} catch (final PermissionDeniedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return 2;
		} catch (final ServerError ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return 3;
		} catch (final DNSException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return 4;
		} catch (final ConnectionRefusedException ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return 5;
		} catch (final Exception ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return -1;
		}

		this.setConnected(true);
		final OmeroKeepClientAlive kca = new OmeroKeepClientAlive(this);
		this.executor = new ScheduledThreadPoolExecutor(1);
		this.executor.scheduleWithFixedDelay(kca, 60, 60, TimeUnit.SECONDS);
		return 0;
	}

	public void disconnect() {
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = null;
		this.setConnected(false);
		this.thumbnailService = null;
		this.adminService = null;
		this.services.clear();
		this.reServices.clear();

		this.secureClient.closeSession();
		this.secureClient = null;
		this.entryEncrypted = null;

		this.secureClient = null;
		this.entryEncrypted = null;
	}

	// TODO Check if needed
	// /**
	// * Returns the images owned by the user currently logged in. We use the
	// * <code>Pojo</code> objects so we don't have to deal directly with the
	// * rtypes.
	// *
	// * @return See above.
	// */
	// public List<ImageData> getImages() throws Exception {
	// final List<ImageData> images = new ArrayList<ImageData>();
	// try {
	// final ParametersI po = new ParametersI();
	// po.exp(omero.rtypes
	// .rlong(this.getAdminService().getEventContext().userId));
	// final IContainerPrx service = this.getContainerService();
	// final List<Image> l = service.getUserImages(po);
	// // stop here if you want to deal with IObject.
	// if (l == null)
	// return images;
	// final Iterator<Image> i = l.iterator();
	// while (i.hasNext()) {
	// images.add(new ImageData(i.next()));
	// }
	// } catch (final Exception e) {
	// // TODO Manage exception
	// new Exception("Cannot retrieve the images", e);
	// }
	// return images;
	// }

	// public List<ImageData> getImages(final DatasetData dataset, List<Long>
	// ids)
	// throws ServerError {
	// final List<ImageData> images = new ArrayList<ImageData>();
	// final ParametersI po = new ParametersI();
	// // po.add(Project.class.getName(), omero.rtypes.rlong(project.getId()));
	// // po.leaves();
	//
	// if (ids == null) {
	// ids = new ArrayList<Long>();
	// final Set<ImageData> set = dataset.getImages();
	// for (final ImageData obj : set) {
	// ids.add(obj.getId());
	// }
	// }
	//
	// final IContainerPrx service = this.getContainerService();
	// final List<Image> objects = service.getImages(Image.class.getName(),
	// ids, po);
	// if (objects == null)
	// return images;
	//
	// final Iterator<Image> i = objects.iterator();
	// while (i.hasNext()) {
	// images.add(new ImageData(i.next()));
	// }
	//
	// return images;
	// }

	public List<DatasetData> getDatasets(final ProjectData project)
	        throws ServerError {
		final List<DatasetData> datasets = new ArrayList<DatasetData>();
		final ParametersI po = new ParametersI();
		// po.add(Project.class.getName(), omero.rtypes.rlong(project.getId()));
		po.exp(omero.rtypes.rlong(project.getOwner().getId()));
		po.leaves();

		final List<Long> ids = new ArrayList<Long>();
		final Set<DatasetData> set = project.getDatasets();
		for (final DatasetData obj : set) {
			ids.add(obj.getId());
		}

		final IContainerPrx service = this.getContainerService();
		final List<IObject> objects = service.loadContainerHierarchy(
		        Dataset.class.getName(), ids, po);
		if (objects == null)
			return datasets;

		final Iterator<IObject> i = objects.iterator();
		while (i.hasNext()) {
			datasets.add(new DatasetData((Dataset) i.next()));
		}

		return datasets;
	}

	public List<ProjectData> getProjects(final ExperimenterData user)
	        throws ServerError {
		final List<ProjectData> projects = new ArrayList<ProjectData>();
		final ParametersI po = new ParametersI();
		po.exp(omero.rtypes.rlong(user.getId()));
		po.noLeaves();
		final IContainerPrx service = this.getContainerService();
		final List<IObject> objects = service.loadContainerHierarchy(
		        Project.class.getName(), null, po);
		if (objects == null)
			return projects;
		final Iterator<IObject> i = objects.iterator();

		while (i.hasNext()) {
			projects.add(new ProjectData((Project) i.next()));
		}
		return projects;
	}

	public ExperimenterData getExperimenter() throws ServerError {
		final IAdminPrx service = this.getAdminService();
		final Long expId = service.getEventContext().userId;
		final Experimenter exp = service.getExperimenter(expId);
		return new ExperimenterData(exp);
	}

	public List<GroupData> getGroups() throws ServerError {
		final List<GroupData> dataGroups = new ArrayList<GroupData>();
		List<ExperimenterGroup> groups = new ArrayList<ExperimenterGroup>();
		final IAdminPrx service = this.getAdminService();
		final Long expId = service.getEventContext().userId;
		groups = service.containedGroups(expId);
		for (final ExperimenterGroup group : groups) {
			final GroupData dataGroup = new GroupData(group);
			dataGroups.add(dataGroup);
		}
		return dataGroups;
	}

	public List<ExperimenterData> getExperimenters(final GroupData group)
	        throws ServerError {
		final List<ExperimenterData> dataExps = new ArrayList<ExperimenterData>();
		final IAdminPrx service = this.getAdminService();
		final Long groupId = group.getId();
		final List<Experimenter> exps = service.containedExperimenters(groupId);
		for (final Experimenter exp : exps) {
			final ExperimenterData expData = new ExperimenterData(exp);
			dataExps.add(expData);
		}
		return dataExps;
	}

	public RenderingEnginePrx getRenderingService(final Long pixelsID)
	        throws ServerError {
		RenderingEnginePrx service = (RenderingEnginePrx) this.reServices
		        .get(pixelsID);
		if (service != null)
			return service;
		service = this.createRenderingService(pixelsID);
		return service;
	}

	/**
	 * Loads the rendering control corresponding to the specified set of pixels.
	 * 
	 * @param pixelsID
	 *            The identifier of the pixels set.
	 * @return See above.
	 * @throws ServerError
	 */
	public RenderingEnginePrx createRenderingService(final long pixelsID)
	        throws ServerError {
		final RenderingEnginePrx service = this.createRenderingService();
		this.reServices.put(pixelsID, service);
		service.lookupPixels(pixelsID);
		if (!(service.lookupRenderingDef(pixelsID))) {
			service.resetDefaults();
			service.lookupRenderingDef(pixelsID);
		}
		service.load();
		return service;
	}

	/**
	 * Retrieves the specified images.
	 * 
	 * @param pixelsID
	 *            The identifier of the images.
	 * @param max
	 *            The maximum length of a thumbnail.
	 * @return See above.
	 * @throws Exception
	 */
	// TODO check if used
	public List<BufferedImage> getThumbnailSet(final List pixelsID,
	        final int max) throws Exception {
		final List<BufferedImage> images = new ArrayList<BufferedImage>();

		try {
			final ThumbnailStorePrx service = this.getThumbnailService();
			final Map<Long, byte[]> results = service
			        .getThumbnailByLongestSideSet(omero.rtypes.rint(max),
			                pixelsID);

			if (results == null)
				return images;
			Entry entry;
			final Iterator i = results.entrySet().iterator();
			BufferedImage image;
			while (i.hasNext()) {
				entry = (Entry) i.next();
				try {
					image = this.createImage((byte[]) entry.getValue());
					if (image != null) {
						images.add(image);
					}
				} catch (final Exception ex) {
					// TODO handle differently
					OmegaLogFileManager.handleUncaughtException(ex);
				}
			}

			return images;

		} catch (final Throwable t) {
			if (this.thumbnailService != null) {
				try {
					this.thumbnailService.close();
				} catch (final Exception ex) {
					// TODO handle differently
					OmegaLogFileManager.handleUncaughtException(ex);
				}
			}
			this.thumbnailService = null;
		}
		return images;
	}

	@Override
	public synchronized byte[] getImageData(final Long pixelsID, final int z,
	        final int t, final int c) {
		RawPixelsStorePrx service = null;
		try {
			service = this.entryEncrypted.createRawPixelsStore();
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		}
		try {
			service.setPixelsId(pixelsID, false);

			return service.getPlane(z, c, t);
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		} finally {
			try {
				service.close();
			} catch (final ServerError ex) {
				// TODO handle differently
				OmegaLogFileManager.handleUncaughtException(ex);
			}
		}
	}

	// TODO check if used
	@Override
	public int getByteWidth(final Long pixelsID) {
		RawPixelsStorePrx service = null;
		try {
			service = this.entryEncrypted.createRawPixelsStore();
			service.setPixelsId(pixelsID, false);
			return service.getByteWidth();
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return 2;
		} finally {
			try {
				service.close();
			} catch (final ServerError ex) {
				// TODO handle differently
				OmegaLogFileManager.handleUncaughtException(ex);
			}
		}
	}

	@Override
	public double getTotalT(final Long pixelsID, final int z, final int t,
	        final int channel) {
		// GLogManager.log("maxT is: " + maxT);

		double sizeT = 0.0;

		try {
			final List<IObject> planeInfoObjects = this.loadPlaneInfo(pixelsID,
			        z, t - 1, channel);

			if (planeInfoObjects.size() > 0) {
				final PlaneInfoI pi = (PlaneInfoI) planeInfoObjects.get(0);

				final RDouble tTemp = pi.getDeltaT();

				if (tTemp != null) {
					sizeT = tTemp.getValue();
				}
			}
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}

		return sizeT;
	}

	// TODO check if used
	public List<IObject> loadPlaneInfo(final long pixelsID, final int z,
	        final int t, final int channel) throws Exception {
		// isSessionAlive();
		final IQueryPrx service = this.entryEncrypted.getQueryService();
		final StringBuilder sb = new StringBuilder();
		final ParametersI param = new ParametersI();
		sb.append("select info from PlaneInfo as info ");
		sb.append("where pixels.id =:id");
		param.addLong("id", pixelsID);

		if (z >= 0) {
			sb.append(" and info.theZ =:z");
			param.map.put("z", omero.rtypes.rint(z));
		}
		if (t >= 0) {
			sb.append(" and info.theT =:t");
			param.map.put("t", omero.rtypes.rint(t));
		}
		if (channel >= 0) {
			sb.append(" and info.theC =:c");
			param.map.put("c", omero.rtypes.rint(channel));
		}
		try {
			final List<IObject> info = service.findAllByQuery(sb.toString(),
			        param);
			return info;
		} catch (final Exception ex) {
			// TODO create the proper exception here
			throw new Exception("Cannot load the plane info for pixels: "
			        + pixelsID, ex);
		}
	}

	@Override
	public int[] renderAsPackedInt(final Long pixelsID, final int t, final int z) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			final PlaneDef planeDef = new PlaneDef();
			// time choice (sliding)
			planeDef.t = t;
			// Z-plan choice
			planeDef.z = z;
			// display the XY plane
			planeDef.slice = omero.romio.XY.value;

			return engine.renderAsPackedInt(planeDef);
		} catch (final ServerError ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		}
	}

	@Override
	public int[] renderAsPackedInt(final Long pixelsID) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			return this.renderAsPackedInt(pixelsID, engine.getDefaultT(),
			        engine.getDefaultZ());
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		}
	}

	@Override
	public byte[] renderCompressed(final Long pixelsID, final int t, final int z) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			final PlaneDef planeDef = new PlaneDef();
			// time choice (sliding)
			planeDef.t = t;
			// Z-plan choice
			planeDef.z = z;
			// display the XY plane
			planeDef.slice = omero.romio.XY.value;

			return engine.renderCompressed(planeDef);
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		}
	}

	@Override
	public byte[] renderCompressed(final Long pixelsID) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			return this.renderCompressed(pixelsID, engine.getDefaultT(),
			        engine.getDefaultZ());
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return null;
		}
	}

	@Override
	public Double computeSizeT(final Long pixelsID, final int pixelSizeT,
	        final int currentMaxT) {
		Double sizeT = null;
		final int maxT = currentMaxT - 1;
		try {
			final List<IObject> planeInfoObjects = this.loadPlaneInfo(pixelsID,
			        0, maxT, 0);
			if ((planeInfoObjects == null) || (planeInfoObjects.size() == 0))
				return sizeT;

			final PlaneInfoI pi = (PlaneInfoI) planeInfoObjects.get(0);

			final RDouble tTemp = pi.getDeltaT();

			if (tTemp != null) {
				sizeT = tTemp.getValue() / pixelSizeT;
			}
		} catch (final Exception ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}
		return sizeT;
	}

	@Override
	public void setActiveChannel(final Long pixelsID, final int channel,
	        final boolean active) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			engine.setActive(channel, active);
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	@Override
	public void setDefaultZ(final Long pixelsID, final int z) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			engine.setDefaultZ(z);
		} catch (final ServerError ex) {
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	@Override
	public int getDefaultZ(final Long pixelsID) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			return engine.getDefaultZ();
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return -1;
		}
	}

	@Override
	public void setDefaultT(final Long pixelsID, final int t) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			engine.setDefaultT(t);
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}

	@Override
	public int getDefaultT(final Long pixelsID) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			return engine.getDefaultT();
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
			return -1;
		}
	}

	@Override
	public void setCompressionLevel(final Long pixelsID, final float compression) {
		try {
			final RenderingEnginePrx engine = this
			        .getRenderingService(pixelsID);
			engine.setCompressionLevel(compression);
		} catch (final ServerError ex) {
			// TODO handle differently
			OmegaLogFileManager.handleUncaughtException(ex);
		}
	}
}