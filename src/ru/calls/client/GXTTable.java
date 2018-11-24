package ru.calls.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import ru.calls.client.services.GreetingService;
import ru.calls.client.services.GreetingServiceAsync;
import ru.calls.client.services.PagingService;
import ru.calls.client.services.PagingServiceAsync;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GXTTable implements IsWidget, EntryPoint
{
	Logger logger = Logger.getLogger("MyLogger");
	GXTGrid gxtGrid = null;

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	private final PagingServiceAsync pagingService = GWT.create(PagingService.class);

	private ToolBar createToolBar()
	{
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Режим выделения : "));

		SimpleComboBox<String> scb = new SimpleComboBox<String>(new StringLabelProvider<String>());
		scb.setTriggerAction(TriggerAction.ALL);
		scb.setEditable(false);
		scb.setWidth(100);
		scb.add("Строка");
		scb.add("Ячейка");
		scb.setValue("Строка");
		// we want to change selection model on select, not value change which fires on blur
		scb.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				boolean cell = event.getSelectedItem().equals("Ячейка");
				gxtGrid.setSelectionModel(cell);
			}
		});
		scb.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {}
		});
		toolBar.add(scb);
		return toolBar;
	}
	@Override
	public Widget asWidget()
	{
		logger.info("GXTTable.asWidget - START");

		ContentPanel panel = new ContentPanel();
		panel.setHeading("Пример GXT Grid");
		panel.setPixelSize(550, 500);
		panel.addStyleName("margin-15");

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setBorders(true);

		ToolBar toolBar = createToolBar();
		gxtGrid = new GXTGrid();		

		PagingToolBar paging = gxtGrid.getPagingToolBar();

		vlc.add(toolBar, new VerticalLayoutData(1,-1));
		vlc.add(gxtGrid, new VerticalLayoutData(1, 1));
		vlc.add(paging , new VerticalLayoutData(1,-1));
		panel.setWidget(vlc);

		return panel;
	}

	/**
	 * This is the entry point method
	 */
	@Override
	public void onModuleLoad()
	{
		final Button sendButton = new Button("Send");
		RootPanel.get().add(sendButton);
		sendButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				greetingService.greetServer("textToServer", new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						new MessageBox("HI", "ERROR").show();
					}

					public void onSuccess(String result) {
						new MessageBox("HI", result).show();
					}
				});
				
			}
		});



		RootPanel.get().add(asWidget());
	}
}
