package ru.calls.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class CallsTable  implements IsWidget, EntryPoint 
{
	Logger logger = Logger.getLogger("MyLogger");
	CallsGrid callsGrid = null;

	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		
	}

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
				callsGrid.setSelectionModel(cell);
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
	public Widget asWidget() {
		logger.info("CallsTable.asWidget - START");
		ContentPanel panel = new ContentPanel();
		panel.setHeading("Пример GXT Grid");
		panel.setPixelSize(550, 500);
		panel.addStyleName("margin-15");

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setBorders(true);

		ToolBar toolBar = createToolBar();
		callsGrid = new CallsGrid();		

		PagingToolBar paging = callsGrid.getPagingToolBar();

		vlc.add(toolBar  , new VerticalLayoutData(1,-1));
		vlc.add(callsGrid, new VerticalLayoutData(1, 1));
		vlc.add(paging   , new VerticalLayoutData(1,-1));
		panel.setWidget(vlc);

		return panel;		
	}

}
