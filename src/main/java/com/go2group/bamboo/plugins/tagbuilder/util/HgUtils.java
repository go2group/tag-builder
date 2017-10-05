package com.go2group.bamboo.plugins.tagbuilder.util;

import java.io.File;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.aragost.javahg.Repository;
import com.aragost.javahg.RepositoryConfiguration;
import com.aragost.javahg.commands.Tag;
import com.aragost.javahg.commands.TagsCommand;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.utils.ConfigUtils;
import com.atlassian.core.util.FileUtils;

public class HgUtils extends SCMUtils {

	public static List<Tag> getHgTagNames(HierarchicalConfiguration configuration) {
		Map<String, Object> configurationMap = ConfigUtils.asObjectMap(configuration);
		
		String repoUrl = (String) configurationMap.get("repository.hg.repositoryUrl");
		String username = (String) configurationMap.get("repository.hg.username");
		String password = (String) configurationMap.get("repository.hg.password");
		
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			try {
				initiliseCipher(Cipher.DECRYPT_MODE);
				password = new String(myCipher.doFinal(Base64.decodeBase64(password)));
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			
			if (repoUrl.startsWith("https://")) {
				repoUrl = repoUrl.replaceFirst("https://", "https://" + username + ":" + password + "@");
			} else if (repoUrl.startsWith("http://")) {
				repoUrl = repoUrl.replaceFirst("http://", "http://" + username + ":" + password + "@");
			}
		}
		
		//TODO Super this method
		
		String tempFolderForHg = System.getProperty("java.io.tmpdir") + "" + System.getProperty("file.separator") + System.currentTimeMillis() + "";
		File clonedRepo = new File(tempFolderForHg);
		Repository remoteRepo = Repository.clone(clonedRepo, repoUrl);
		
		List<Tag> tags = TagsCommand.on(remoteRepo).execute();

		// Close the commandserver before you can delete the repository
		remoteRepo.close();
		FileUtils.recursiveDelete(clonedRepo);
		
		return tags;
	}
}
