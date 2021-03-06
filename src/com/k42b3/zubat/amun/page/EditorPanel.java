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

package com.k42b3.zubat.amun.page;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javafx.application.Platform;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringEscapeUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.k42b3.zubat.basic.WebviewEditorPanelAbstract;
import com.k42b3.zubat.model.Page;

/**
 * EditorPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class EditorPanel extends WebviewEditorPanelAbstract
{
	protected RSyntaxTextArea textarea;
	
	public EditorPanel(Page page) throws Exception
	{
		super(page);
	}

	public JComponent getEditorComponent() throws Exception
	{
		textarea = new RSyntaxTextArea();
		textarea.setText(form.getRequestFields().get("content").getValue());
		textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);

		return new RTextScrollPane(textarea);
	}

	protected JMenuBar getMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		
		JMenuItem itemPreview = new JMenuItem("Preview");
		itemPreview.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemPreview.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				preview();
			}
			
		});
		menu.add(itemPreview);

		JMenuItem itemPublish= new JMenuItem("Publish");
		itemPublish.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		itemPublish.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
			
		});
		menu.add(itemPublish);

		menuBar.add(menu);
		
		return menuBar;
	}

	protected JComponent getBottomBar()
	{
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				preview();
			}

		});

		JButton btnPublish = new JButton("Publish");
		btnPublish.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				save();
			}

		});

		buttons.add(btnPreview);
		buttons.add(btnPublish);
		
		return buttons;
	}

	protected void setRequestFields()
	{
		form.getRequestFields().get("content").setValue(textarea.getText());
	}

	protected void preview()
	{
		Platform.runLater(new Runnable(){

			public void run() 
			{
				String js = "";
				js+= "var html = '" + StringEscapeUtils.escapeEcmaScript(textarea.getText()) + "';";
				js+= "$('." + (recordId > 0 ? "amun-service-page-content" : "amun-service-page") + "').html(html);";

				webengine.executeScript(js);
			}

		});
	}
}
