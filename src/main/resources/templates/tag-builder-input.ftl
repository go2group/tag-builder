<html>
	<head>
		<title>${plan.key}: [@ui.header pageKey='build.tags' /]</title>
		<meta name="tab" content="tagbuilder"/>
	</head>
	<body>
		[@ui.header pageKey='build.tags' /]
		<table class="aui aui-zebra" id="dashboard">
			<thead>
				<tr>
					<td>Repository Name</td>
					<td>Repository Type</td>
					<td>Repository Tags</td>
				<tr>
			</thead>
			<tbody>
				[#list (repositoryDefinitions)! as repositoryDefinition]
					<tbody class="projectHeader" style="display: table-row-group;">
						<tr>
							<td>
								<a href="${req.contextPath}/chain/admin/config/editRepository.action?planKey=${plan.key}&repositoryId=${repositoryDefinition.id}">${repositoryDefinition.name}<a/>
							</td>
							<td>${repositoryDefinition.pluginKey}</td>
							<td width="40%">
								<form method="POST" action="${req.contextPath}/build/buildTag.action" id="buildTag-${plan.key}-${repositoryDefinition.id}" name="buildTag-${plan.key}-${repositoryDefinition.id}" class="aui top-label">
									<input type="hidden" id="planKey" name="planKey" value="${planKey}" />
									<input type="hidden" id="repositoryId" name="repositoryId" value="${repositoryDefinition.id}" />
									<input type="hidden" id="formId" name="formId" value="#buildTag-${plan.key}-${repositoryDefinition.id}" />
									<div class="field-group">
										<div style="float:left">
										[#assign tags = action.getTags(repositoryDefinition)]
										[#if repositoryDefinition.pluginKey == "com.atlassian.bamboo.plugins.atlassian-bamboo-plugin-git:git"]
											<input type="hidden" id="repoType" name="repoType" value="git" />
											<select id="customRevision" name="customRevision" class="select">
											[#list tags as tag]
												<option value="${tag.name}">${tag.name}</option>
											[/#list]
											</select>
											
											[#elseif repositoryDefinition.pluginKey == "com.atlassian.bamboo.plugin.system.repository:svn"]
											<input type="hidden" id="repoType" name="repoType" value="svn" />
											<select id="customRevision" name="customRevision" class="select">
											[#list tags as tag]
												<option value="${tag.svnRevision}-${tag.name}">${tag.name}</option>
											[/#list]
											</select>
											
											[#elseif repositoryDefinition.pluginKey == "com.atlassian.bamboo.plugins.atlassian-bamboo-plugin-mercurial:hg"]
											<input type="hidden" id="repoType" name="repoType" value="hg" />
											<select id="customRevision" name="customRevision" class="select">
											[#list tags as tag]
												<option value="${tag.name}">${tag.name}</option>
											[/#list]
											</select>
											
											[#elseif repositoryDefinition.pluginKey == "com.atlassian.bamboo.plugins.stash.atlassian-bamboo-plugin-stash:stash-rep"]
											<input type="hidden" id="repoType" name="repoType" value="git" />
											<select id="customRevision" name="customRevision" class="select">
											[#list tags as tag]
												<option value="${tag.name}">${tag.name}</option>
											[/#list]
											</select>
										[/#if]
										</div>
										[#if tags?has_content]
										<div style="float:right">
											<input type="submit" name="save" value="Build Tag" class="aui-button aui-button-primary" id="buildTagButton">
										</div>
										[/#if]
									</div>
								</form>
							</td>
						</tr>
					</tbody>
				[/#list]
			</tbody>
		</table>
	</body>
</html>