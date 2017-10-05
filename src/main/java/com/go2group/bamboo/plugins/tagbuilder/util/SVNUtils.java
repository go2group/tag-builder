package com.go2group.bamboo.plugins.tagbuilder.util;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc2.ISvnObjectReceiver;
import org.tmatesoft.svn.core.wc2.SvnList;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.atlassian.bamboo.utils.ConfigUtils;

public class SVNUtils extends SCMUtils {

	public static List<SVNDirEntry> getSVNTagNames(HierarchicalConfiguration configuration) {
		Map<String, Object> configurationMap = ConfigUtils.asObjectMap(configuration);

		final List<SVNDirEntry> tags = new ArrayList<SVNDirEntry>();

		String repoUrl = (String) configurationMap.get("repository.svn.repositoryUrl");
		repoUrl = repoUrl.replace("/trunk", "/tags");

		DAVRepositoryFactory.setup();

		try {
			SVNURL url = SVNURL.parseURIEncoded(repoUrl);
			SVNClientManager clientManager = SVNClientManager.newInstance();

			SvnOperationFactory operationFactory = new SvnOperationFactory();
			SvnList list = null;
			if (configurationMap.get("repository.svn.authType").equals("password")) {
				try {
					initiliseCipher(Cipher.DECRYPT_MODE);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
				
				String username = (String) configurationMap.get("repository.svn.username");
				String password = "";
				try {
					password = new String(myCipher.doFinal(Base64.decodeBase64((String) configurationMap.get("repository.svn.userPassword"))));
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
				
				operationFactory.setAuthenticationManager(new BasicAuthenticationManager(username, password));
			}
			
			list = operationFactory.createList();
			list.addTarget(SvnTarget.fromURL(url));
			list.setReceiver(new ISvnObjectReceiver<SVNDirEntry>() {
				public void receive(SvnTarget target, SVNDirEntry object) throws SVNException {
					tags.add(object);
				}
			});
			list.run();

			System.out.println();
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return tags;
	}
}