package com.go2group.bamboo.plugins.tagbuilder.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.atlassian.bamboo.utils.ConfigUtils;

public class GitUtils extends SCMUtils {
	
	public static Git getGitRepoWithPassword(HierarchicalConfiguration configuration) {
		try {
			initiliseCipher(Cipher.DECRYPT_MODE);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> configurationMap = ConfigUtils.asObjectMap(configuration);
		
		String password = "";
		try {
			password = new String(myCipher.doFinal(Base64.decodeBase64((String) configurationMap.get("repository.git.password"))));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		String repoUrl = (String) configurationMap.get("repository.git.repositoryUrl");
		String authenticationType = (String) configurationMap.get("repository.git.authenticationType");
		String username = (String) configurationMap.get("repository.git.username");
		
		// Regex to get filename: "^(.*\\/)(.*)\\.(.*)$" @ 3rd group
		String tempFolderForGit = System.getProperty("java.io.tmpdir") + "" + System.getProperty("file.separator") + System.currentTimeMillis() + "";
		
		Git git = null;
		try {
			git = Git.cloneRepository().setURI(repoUrl).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
					.setDirectory(new File(tempFolderForGit)).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} finally {
			File clonedRepo = new File(tempFolderForGit);
			clonedRepo.delete();
		}
		
		return git;
	}
}
