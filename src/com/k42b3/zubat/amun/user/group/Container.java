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

package com.k42b3.zubat.amun.user.group;

import java.util.Map;

import javax.swing.JComponent;

import com.k42b3.neodym.data.Endpoint;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerExceptionEvent;
import com.k42b3.zubat.container.ContainerSuccessEvent;

/**
 * Container
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Container extends com.k42b3.zubat.basic.Container
{
	public Container(Endpoint api) throws Exception
	{
		super(api);
	}

	protected JComponent getFormPanel(Endpoint api, int type, int id, Map<String, String> params)
	{
		try
		{
			FormPanel panel = new FormPanel(api, type, id, params);
			panel.addContainerListener(new ContainerEventListener() {
				
				public void containerEvent(ContainerEvent event)
				{
					if(event instanceof ContainerSuccessEvent)
					{
						tp.setSelectedIndex(0);

						setSelectedId(0);
					}
					else if(event instanceof ContainerExceptionEvent)
					{
						// exception occured
					}
				}
				
			});

			return panel;
		}
		catch(Exception e)
		{
			return getExceptionPanel(e);
		}
	}
}
