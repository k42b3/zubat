/**
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011-2013 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of zubat. zubat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * zubat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zubat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Service;
import com.k42b3.neodym.Services;
import com.k42b3.neodym.TrafficItem;
import com.k42b3.neodym.TrafficListenerInterface;
import com.k42b3.neodym.oauth.Oauth;
import com.k42b3.neodym.oauth.OauthProvider;

/**
 * Auth
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Auth extends JFrame
{
	private static final long serialVersionUID = 1L;

	private Services availableServices;
	private Oauth oauth;
	private Http http;

	private TrafficTableModel trafficTm;

	private Logger logger = Logger.getLogger("com.k42b3.zubat");
	
	public Auth()
	{
		this.setTitle("zubat (version: " + Zubat.version + ")");
		this.setPreferredSize(new Dimension(400, 200));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		try
		{
			// buttons
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

			JButton btnLogin = new JButton("Login");
			btnLogin.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					try
					{
						if(oauth.requestToken())
						{
							if(oauth.authorizeToken())
							{
								if(oauth.accessToken())
								{
									JOptionPane.showMessageDialog(null, "You have successful authenticated");

									saveConfig(oauth.getToken(), oauth.getTokenSecret());
								}
							}
						}
					}
					catch(Exception ex)
					{
						logger.warning(ex.getMessage());
					}
				}

			});

			JButton btnClose = new JButton("Close");
			btnClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}

			});

			buttons.add(btnLogin);
			buttons.add(btnClose);

			this.add(buttons, BorderLayout.SOUTH);

			// status
			JLabel status;

			if(Configuration.getInstance().getConsumerKey().isEmpty())
			{
				status = new JLabel("Please provide a consumer key in the configuration.");
				
				btnLogin.setEnabled(false);
			}
			else if(Configuration.getInstance().getConsumerSecret().isEmpty())
			{
				status = new JLabel("Please provide a consumer secret in the configuration.");
				
				btnLogin.setEnabled(false);
			}
			else
			{
				status = new JLabel("Click on \"Login\" to start the authentication.");
			}

			status.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			this.add(status, BorderLayout.NORTH);

			// traffic panel
			trafficTm = new TrafficTableModel();

			http = new Http(new TrafficListenerInterface(){

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});

			TrafficPanel trafficPanel = new TrafficPanel(trafficTm);

			this.add(trafficPanel, BorderLayout.CENTER);

			// oauth config
			availableServices = new Services(http, Configuration.getInstance().getBaseUrl());
			availableServices.discover();

			Service request = availableServices.getService("http://oauth.net/core/1.0/endpoint/request");
			Service authorization = availableServices.getService("http://oauth.net/core/1.0/endpoint/authorize");
			Service access = availableServices.getService("http://oauth.net/core/1.0/endpoint/access");

			if(request == null)
			{
				throw new Exception("Could not find request service");
			}

			if(authorization == null)
			{
				throw new Exception("Could not find authorization service");
			}

			if(access == null)
			{
				throw new Exception("Could not find access service");
			}

			OauthProvider provider = new OauthProvider(request.getUri(), authorization.getUri(), access.getUri(), Configuration.getInstance().getConsumerKey(), Configuration.getInstance().getConsumerSecret());
			oauth = new Oauth(http, provider);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());

			Zubat.handleException(e);
		}
	}
	
	private void saveConfig(String token, String tokenSecret)
	{
		try
		{
			// load dom
			Document doc = Configuration.loadDocument();

			// add token / tokenSecret element
			Element tokenElement = (Element) doc.getElementsByTagName("token").item(0);
			Element tokenSecretElement = (Element) doc.getElementsByTagName("tokenSecret").item(0);

			if(tokenElement != null)
			{
				tokenElement.setTextContent(token);
			}
			else
			{
				tokenElement = doc.createElement("token");
				tokenElement.setTextContent(token);

				doc.appendChild(tokenElement);
			}

			if(tokenSecretElement != null)
			{
				tokenSecretElement.setTextContent(tokenSecret);
			}
			else
			{
				tokenSecretElement = doc.createElement("tokenSecret");
				tokenSecretElement.setTextContent(tokenSecret);

				doc.appendChild(tokenSecretElement);
			}

			// save dom
			Configuration.saveDocument(doc);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "The following error occured: " + e.getMessage() + "\nNevertheless you can use the obtained token and token secret by adding them manually to the configuration file.\n\nToken: " + token + "\nToken secret: " + tokenSecret);

			Zubat.handleException(e);
		}
	}
}
