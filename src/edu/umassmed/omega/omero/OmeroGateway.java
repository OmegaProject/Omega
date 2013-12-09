package edu.umassmed.omega.omero;

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
import java.util.logging.Level;

import javax.imageio.ImageIO;

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
import omero.model.Image;
import omero.model.Project;
import omero.sys.ParametersI;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.ImageData;
import pojos.ProjectData;
import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;

import com.galliva.gallibrary.GLogManager;

/**
 * Entry point to access the services. Code should be provided to keep those
 * services alive.
 */
public class OmeroGateway {
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

	/** Flag indicating if you are connected or not. */
	private boolean connected;

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
	private int thumbRetrieval;

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
	private ThumbnailStorePrx getThumbService() {
		try {
			if (this.thumbRetrieval == OmeroGateway.MAX_RETRIEVAL) {
				this.thumbRetrieval = 0;
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
			this.thumbRetrieval++;
			return this.thumbnailService;
		} catch (final Throwable e) {
			// TODO Manage exception
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
		} catch (final Throwable e) {
			// TODO Manage exception
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
		} catch (final Throwable e) {
			// TODO Manage exception
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
	private RenderingEnginePrx getRenderingService() {
		try {
			final RenderingEnginePrx engine = this.entryEncrypted
			        .createRenderingEngine();
			return engine;
		} catch (final Throwable e) {
			// TODO Manage exception
		}

		// TODO Manage Null Case
		return null;
	}

	/** Creates a new instance. */
	public OmeroGateway() {
		this.connected = false;
		this.services = new ArrayList<ServiceInterfacePrx>();
		this.reServices = new HashMap<Long, StatefulServiceInterfacePrx>();
	}

	/** Keeps the services alive. */
	void keepSessionAlive() {
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
	public boolean connect(final OmeroLoginCredentials loginCred,
	        final OmeroServerInformation serverInfo)
	        throws CannotCreateSessionException, PermissionDeniedException,
	        ServerError {
		// read login file
		// parse
		this.secureClient = new client(serverInfo.getHostName(),
		        serverInfo.getPort());

		try {
			this.entryEncrypted = this.secureClient.createSession(
			        loginCred.getUserName(), loginCred.getPassword());
			this.connected = true;
			final OmeroKeepClientAlive kca = new OmeroKeepClientAlive(this);
			this.executor = new ScheduledThreadPoolExecutor(1);
			this.executor.scheduleWithFixedDelay(kca, 60, 60, TimeUnit.SECONDS);
		} catch (final Exception ext) {
			throw ext;
		}
		return this.connected;
	}

	public void disconnect() {
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = null;
		this.connected = false;
		this.thumbnailService = null;
		this.adminService = null;
		this.services.clear();
		this.reServices.clear();
		try {
			this.secureClient.closeSession();
			this.secureClient = null;
			this.entryEncrypted = null;
		} catch (final Exception e) {
			// session already dead.
		} finally {
			this.secureClient = null;
			this.entryEncrypted = null;
		}
	}

	/**
	 * Returns the images owned by the user currently logged in. We use the
	 * <code>Pojo</code> objects so we don't have to deal directly with the
	 * rtypes.
	 * 
	 * @return See above.
	 */
	public List<ImageData> getImages() throws Exception {
		final List<ImageData> images = new ArrayList<ImageData>();
		try {
			final ParametersI po = new ParametersI();
			po.exp(omero.rtypes
			        .rlong(this.getAdminService().getEventContext().userId));
			final IContainerPrx service = this.getContainerService();
			final List<Image> l = service.getUserImages(po);
			// stop here if you want to deal with IObject.
			if (l == null)
				return images;
			final Iterator<Image> i = l.iterator();
			while (i.hasNext()) {
				images.add(new ImageData(i.next()));
			}
		} catch (final Exception e) {
			// TODO Manage exception
			new Exception("Cannot retrieve the images", e);
		}
		return images;
	}

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
		// final List<DatasetData> projectDatasets = new
		// ArrayList<DatasetData>();
		// for (final DatasetData dataset : datasets) {
		// final Set<ProjectData> projects = dataset.getProjects();
		// if (projects.contains(project)) {
		// projectDatasets.add(dataset);
		// }
		// }
		// return projectDatasets;
	}

	/**
	 * Returns the datasets owned by the user currently logged in. We use the
	 * <code>Pojo</code> objects so we don't have to deal directly with the
	 * rtypes.
	 * 
	 * @param ids
	 * @return See above.
	 * @throws Exception
	 */
	public List<DatasetData> getDatasets(final List<Long> ids)
	        throws ServerError {
		final List<DatasetData> datasets = new ArrayList<DatasetData>();
		final ParametersI po = new ParametersI();
		po.exp(omero.rtypes
		        .rlong(this.getAdminService().getEventContext().userId));
		if ((ids == null) || (ids.size() == 0)) {
			po.noLeaves();
		} else {
			po.leaves();
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

	/**
	 * Loads the rendering control corresponding to the specified set of pixels.
	 * 
	 * @param pixelsID
	 *            The identifier of the pixels set.
	 * @return See above.
	 */
	public RenderingEnginePrx loadRenderingControl(final long pixelsID)
	        throws Exception {
		try {
			RenderingEnginePrx service = (RenderingEnginePrx) this.reServices
			        .get(pixelsID);
			if (service != null)
				return service;
			service = this.getRenderingService();
			this.reServices.put(pixelsID, service);
			service.lookupPixels(pixelsID);
			if (!(service.lookupRenderingDef(pixelsID))) {
				service.resetDefaults();
				service.lookupRenderingDef(pixelsID);
			}
			service.load();
			return service;
		} catch (final Throwable t) {
			// TODO Manage exception
			new Exception("Cannot load rendering engine", t);
		}
		return null;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BufferedImage> getThumbnailSet(final List pixelsID,
	        final int max) throws Exception {
		final List<BufferedImage> images = new ArrayList<BufferedImage>();

		try {
			final ThumbnailStorePrx service = this.getThumbService();
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
				} catch (final Exception e) {
					// TODO Manage exception
				}
			}

			return images;

		} catch (final Throwable t) {
			if (this.thumbnailService != null) {
				try {
					this.thumbnailService.close();
				} catch (final Exception e) {
					// TODO Manage exception
				}
			}
			this.thumbnailService = null;
		}
		return images;
	}

	public synchronized byte[] getPlane(final long pixelsID, final int z,
	        final int t, final int c) throws Exception {
		final RawPixelsStorePrx service = this.entryEncrypted
		        .createRawPixelsStore();
		try {
			service.setPixelsId(pixelsID, false);

			return service.getPlane(z, c, t);
		} catch (final Throwable e) {
			// TODO Manage exception
			GLogManager.log(
			        String.format("%s: %s", "cannot retrieve the plane",
			                e.toString()), Level.SEVERE);
			new Exception("cannot retrieve the plane " + "(z=" + z + ", t=" + t
			        + ", c=" + c + ") for pixelsID:  " + pixelsID, e);
		} finally {
			service.close();
		}

		return null;
	}

	public int getByteWidht(final long pixelsID) {
		RawPixelsStorePrx service = null;
		try {
			service = this.entryEncrypted.createRawPixelsStore();
			service.setPixelsId(pixelsID, false);
			return service.getByteWidth();
		} catch (final ServerError e) {
			// TODO Manage exception
			return 2;
		} finally {
			try {
				service.close();
			} catch (final ServerError e) {
				// TODO Manage exception
			}
		}
	}

	public List<IObject> loadPlaneInfo(final long pixelsID, final int z,
	        final int t, final int channel) throws Exception {
		// isSessionAlive();
		final IQueryPrx service = this.entryEncrypted.getQueryService();
		StringBuilder sb;
		ParametersI param;
		sb = new StringBuilder();
		param = new ParametersI();
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
		} catch (final Exception e) {
			// TODO Manage exception
			throw new Exception("Cannot load the plane info for pixels: "
			        + pixelsID, e);
		}
	}

	public boolean isConnected() {
		return this.connected;
	}
}