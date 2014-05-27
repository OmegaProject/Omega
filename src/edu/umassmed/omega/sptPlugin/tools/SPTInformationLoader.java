package edu.umassmed.omega.sptPlugin.tools;

public abstract class SPTInformationLoader
{
	protected SPTExecutionInfoHandler	executionInfoHandler	= null;

	public abstract void initLoader();

	public abstract SPTExecutionInfoHandler loadInformation();

	public abstract void closeLoader();
}
