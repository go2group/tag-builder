package com.go2group.bamboo.plugins.tagbuilder.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.go2group.bamboo.plugins.tagbuilder.bean.StashTags;
import com.go2group.bamboo.plugins.tagbuilder.bean.TBTags;
import com.go2group.bamboo.plugins.tagbuilder.bean.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StashUtils extends SCMUtils {
	
	public static List<TBTags> getStashRepo(ApplicationLinkService applicationLinkService, HierarchicalConfiguration configuration) {
		String stashServerId = configuration.getString("repository.stash.server");
		String projectKey = configuration.getString("repository.stash.projectKey");
		String slugKey = configuration.getString("repository.stash.repositorySlug");
		
		List<TBTags> tags = new LinkedList<TBTags>();
		
		Boolean isLastPage = false;
		Integer nextPageStart = null;
		ApplicationLink stashLink = null;
		while (!isLastPage) {
			try {
				stashLink = applicationLinkService.getApplicationLink(new ApplicationId(stashServerId));
				if (stashLink != null) {
					String resourceUrl = null;
					if (nextPageStart == null) {
						resourceUrl = "rest/api/1.0/projects/" + projectKey + "/repos/" + slugKey + "/tags";
					} else {
						resourceUrl = "rest/api/1.0/projects/" + projectKey + "/repos/" + slugKey + "/tags?start=" + nextPageStart.toString();
					}
					
					ApplicationLinkRequestFactory authenticatedRequestFactory = stashLink.createAuthenticatedRequestFactory();
					ApplicationLinkRequest request = authenticatedRequestFactory.createRequest(Request.MethodType.GET, resourceUrl);
					String resp = request.execute();
					
					Gson gson = new GsonBuilder().create();
					StashTags stashTags = gson.fromJson(resp, StashTags.class);
					for (Value tag : stashTags.getValues()) {
						tags.add(new TBTags(tag.getId()));
					}
					isLastPage = stashTags.getIsLastPage();
					nextPageStart = stashTags.getNextPageStart();
				}
			} catch (TypeNotInstalledException e) {
				e.printStackTrace();
			} catch (CredentialsRequiredException e) {
				e.printStackTrace();
			} catch (ResponseException e) {
				e.printStackTrace();
			}
		}
		
		
		return tags;
	}
}
