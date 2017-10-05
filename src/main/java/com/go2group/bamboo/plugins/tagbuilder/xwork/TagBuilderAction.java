package com.go2group.bamboo.plugins.tagbuilder.xwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.aragost.javahg.commands.Tag;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.bamboo.plan.ExecutionRequestResult;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionConfig;
import com.atlassian.bamboo.plan.PlanExecutionConfigImpl;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.bamboo.repository.RepositoryDefinition;
import com.atlassian.bamboo.repository.RepositoryDefinitionManager;
import com.atlassian.bamboo.v2.trigger.ManualBuildDetectionAction;
import com.atlassian.bamboo.v2.ww2.build.ParameterisedManualBuild;
import com.go2group.bamboo.plugins.tagbuilder.bean.TBTags;
import com.go2group.bamboo.plugins.tagbuilder.util.GitUtils;
import com.go2group.bamboo.plugins.tagbuilder.util.HgUtils;
import com.go2group.bamboo.plugins.tagbuilder.util.SVNUtils;
import com.go2group.bamboo.plugins.tagbuilder.util.StashUtils;
import com.google.common.collect.Lists;

public class TagBuilderAction extends ParameterisedManualBuild {

	private static final long serialVersionUID = -1474144404648173788L;

	private ApplicationLinkService applicationLinkService;
	private RepositoryDefinitionManager repositoryDefinitionManager;

	private List<RepositoryDefinition> repositoryDefinitions;
	private Plan plan;
	public String repoType;
	
	public TagBuilderAction() {
	}

	public String doExecute() throws Exception {
		if (repositoryDefinitions == null) {
			repositoryDefinitions = repositoryDefinitionManager.getRepositoryDefinitionsForPlan(getImmutablePlan());
		}
		return SUCCESS;
	}

	public String doDefault() throws Exception {
		return SUCCESS;
	}
	
	public String doInput() throws Exception {
		return SUCCESS;
	}
	
	public String buildTag() throws Exception {
		Map<String, String> params = calculateParams();
		final PlanExecutionConfig planExecutionConfig;

		if (StringUtils.isEmpty(getSelectedStage())) {
			planExecutionConfig = new PlanExecutionConfigImpl(PlanExecutionConfig.PlanExecutionType.REGULAR);
		} else {
			planExecutionConfig = new PlanExecutionConfigImpl(PlanExecutionConfig.PlanExecutionType.ENFORCE_MANUAL_AS_AUTOMATIC).setChain(
					(ImmutableChain) getImmutablePlan()).setLatestManualStageForAutomaticExecution(getSelectedStage());
		}

		if (isBranchMergePushOverride()) {
			planExecutionConfig.setBranchMergePushOverride(isBranchMergePushOverride());
		}
		
		if (repoType.equals("svn")) {
			String customRevisionFromForm = params.get(ManualBuildDetectionAction.CUSTOM_REVISION_PARAMETER);
			String[] customRevisionFromFormParts = customRevisionFromForm.split("-");
			String svnRevision = customRevisionFromFormParts[0];
			String svnTagName = customRevisionFromFormParts[1];
			if (StringUtils.isNotBlank(svnTagName)) {
				customVariables.put("SVN Tag Name", svnTagName);
			}
			
			params.put(ManualBuildDetectionAction.CUSTOM_REVISION_PARAMETER, svnRevision);
		}
		
		ExecutionRequestResult result = planExecutionManager.startManualExecution(getImmutableChain(), planExecutionConfig, getUser(), params, customVariables);
		
		PlanResultKey planResultKey = result.getPlanResultKey();
		if (planResultKey != null) {
			buildNumber = planResultKey.getBuildNumber();
			return SUCCESS;
		} else {
			addErrorCollection(result.getErrors());
			return ERROR;
		}
	}
	
	public List<TBTags> getTags(RepositoryDefinition repositoryDefinition) {
		//String xmlData = repositoryDefinition.getXmlData();
		HierarchicalConfiguration configuration = repositoryDefinition.getConfiguration();
		String repositoryTypeKey = repositoryDefinition.getPluginKey();
		
		if (repositoryTypeKey.contains("svn")) {
			List<SVNDirEntry> svnTags = SVNUtils.getSVNTagNames(configuration);
			
			return convertFromSVN(svnTags);
		} else if (repositoryTypeKey.contains("git")) {
			List<Ref> tags = new ArrayList<Ref>();
			
			Git git = GitUtils.getGitRepoWithPassword(configuration);
			
			ListTagCommand taglistcmd = git.tagList();
			try {
				tags = taglistcmd.call();
			} catch (GitAPIException gapie) {
				LOG.error("There was an error looking for tags from the Git repository", gapie);
			}
			
			return convertFromGit(tags);
		} else if (repositoryTypeKey.contains("hg")) {
			List<Tag> hgTags = Lists.newArrayList();
			hgTags.addAll(HgUtils.getHgTagNames(configuration));
			
			return convertFromHg(hgTags);
		} else if (repositoryTypeKey.contains("stash-rep")) {
			List<TBTags> tags = StashUtils.getStashRepo(applicationLinkService, configuration);
			
			return tags;
		}
		
		return new ArrayList<TBTags>();
	}
	
	private List<TBTags> convertFromGit(List<Ref> gitTags) {
		List<TBTags> results = new ArrayList<TBTags>(gitTags.size());
		
		for (Ref gitTag : gitTags) {
			TBTags tbTag = new TBTags(gitTag.getName());
			results.add(tbTag);
		}
		
		return results;
	}
	
	private List<TBTags> convertFromSVN(List<SVNDirEntry> svnTags) {
		List<TBTags> results = new ArrayList<TBTags>(svnTags.size());
		
		for (SVNDirEntry svnTag : svnTags) {
			if (StringUtils.isNotBlank(svnTag.getName())) {
				TBTags tbTag = new TBTags(svnTag.getName(), svnTag.getRevision());
				results.add(tbTag);
			}
		}
		
		return results;
	}
	
	private List<TBTags> convertFromHg(List<Tag> hgTags) {
		List<TBTags> results = new ArrayList<TBTags>(hgTags.size());
		
		for (Tag hgTag : hgTags) {
			TBTags tbTag = new TBTags(hgTag.getName());
			results.add(tbTag);
		}
		
		return results;
	}
	
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	
	public ApplicationLinkService getApplicationLinkService() {
		return applicationLinkService;
	}
	public void setApplicationLinkService(ApplicationLinkService applicationLinkService) {
		this.applicationLinkService = applicationLinkService;
	}

	public List<RepositoryDefinition> getRepositoryDefinitions() {
		return repositoryDefinitions;
	}
	public void setRepositoryDefinitionManager(final RepositoryDefinitionManager repositoryDefinitionManager) {
		this.repositoryDefinitionManager = repositoryDefinitionManager;
	}
	
	public String getRepoType() {
		return repoType;
	}
	public void setRepoType(String repoType) {
		this.repoType = repoType;
	}
}
