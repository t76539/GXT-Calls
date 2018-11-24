package ru.calls.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ru.calls.client.services.PagingService;
import ru.calls.client.services.PagingServiceAsync;
import ru.calls.shared.Post;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

public class GXTGrid implements IsWidget
{
	Logger logger = Logger.getLogger("MyLogger");

	Grid<Post>       grid          = null;
	ListStore<Post>  store         = null;
	PagingToolBar    pagingToolBar = null;
	PagingLoader<PagingLoadConfig, PagingLoadResult<Post>> loader = null;
	RpcProxy<PagingLoadConfig, PagingLoadResult<Post>>     proxy  = null;

	public GXTGrid()
	{
		createGrid();
	}

	@Override
	public Widget asWidget()
	{
		return grid;
	}

	public PagingToolBar getPagingToolBar()
	{
		return pagingToolBar;		
	}

	private void createPagingToolBar()
	{
		pagingToolBar = new PagingToolBar(15);
		pagingToolBar.getElement().getStyle().setProperty("borderBottom", "none");
		pagingToolBar.bind(loader);
	}

	private void createLoader()
	{
		logger.info("GXTGrid: createLoader");
		final PagingServiceAsync service = GWT.create(PagingService.class);
		proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<Post>>() {
			@Override
			public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<Post>> callback) {
				logger.info("GXTGrid: proxy.load");
				service.getPosts(loadConfig, callback);
				logger.info("GXTGrid: loadConfig=" + loadConfig.toString());
			}
		};

		store = new ListStore<Post>(new ModelKeyProvider<Post>() {
			@Override
			public String getKey(Post item) {
				logger.info("GXTGrid: story.getKey");
				return "" + item.getId();
			}
		});

		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<Post>>(proxy);
		loader.setRemoteSort(true);
		LoadResultListStoreBinding <PagingLoadConfig, Post, PagingLoadResult<Post>> lrlsb;
		lrlsb =	new LoadResultListStoreBinding<PagingLoadConfig, Post, PagingLoadResult<Post>>(store);
		loader.addLoadHandler(lrlsb);
	}

	private void createGrid()
	{
		createLoader();
		createPagingToolBar();

		PostProperties props = GWT.create(PostProperties.class);

		IdentityValueProvider<Post> identity = new IdentityValueProvider<Post>();
		final CheckBoxSelectionModel<Post> sm = new CheckBoxSelectionModel<Post>(identity) {
			@Override
			protected void onRefresh(RefreshEvent event) {
				// this code selects all rows when paging if the header checkbox is selected
				if (isSelectAllChecked())
					selectAll();
				super.onRefresh(event);
			}
		};
		RowExpander<Post> expander = new RowExpander<Post>(identity, new AbstractCell<Post>() {	    	 
			@Override
			public void render(Context context, Post value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>" + value.getUsername() + "</b></p>");
				sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'>" + value.getSubject() + "</p>");
			}
		});

		ColumnConfig<Post, String> forumColumn    = new ColumnConfig<Post, String>(props.forum()   , 250, "Forum"   );
		ColumnConfig<Post, String> usernameColumn = new ColumnConfig<Post, String>(props.username(), 100, "Username");
		ColumnConfig<Post, String> subjectColumn  = new ColumnConfig<Post, String>(props.subject() , 120, "Subject" );
		ColumnConfig<Post, Date  > dateColumn     = new ColumnConfig<Post, Date  >(props.date()    , 100, "Date"    );
		dateColumn.setCell(new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));

		subjectColumn.setCell(new AbstractCell<String>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				int row = context.getIndex();
				Post post = grid.getStore().get(row);
				if (post.getSubject().startsWith("KEM")) {
					String firstLine  = value.substring(0, value.indexOf(" "));
					String secondLine = value.substring(value.indexOf(" ") + 1);
					String temp = "<div style=\"white-space: nowrap;\">" + 
							"<span style=\"color:gray;\">" + firstLine + "</span>" +  
							"<br>"    +
							"<span style=\"color:red;\">" + secondLine + "</span>" +
							"</div>";
					sb.appendHtmlConstant(temp);
				} else
					sb.appendHtmlConstant("<div style=\"white-space: normal;\" >" + value + "</div>");
			}
		});	    

		List<ColumnConfig<Post, ?>> l = new ArrayList<ColumnConfig<Post, ?>>();
		l.add(sm.getColumn());
		l.add(expander);
		l.add(forumColumn);
		l.add(usernameColumn);
		l.add(subjectColumn);
		l.add(dateColumn);

		ColumnModel<Post> cm = new ColumnModel<Post>(l);

		grid = new Grid<Post>(store, cm){
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						logger.info("GXTGrid: get");
						loader.load();
					}
				});
			}
		};
		grid.setSelectionModel(sm);
		grid.getView().setForceFit(true);
		grid.setLoadMask(true);
		grid.setLoader(loader);

		expander.initPlugin(grid);	    
	}

	public void setSelectionModel(final boolean cell_celection)
	{
		if (cell_celection) {
			CellSelectionModel<Post> c = new CellSelectionModel<Post>();
			c.addCellSelectionChangedHandler(new CellSelectionChangedHandler<Post>() {
				@Override
				public void onCellSelectionChanged(CellSelectionChangedEvent<Post> event) {}
			});

			grid.setSelectionModel(c);
		} else {
			grid.setSelectionModel(new GridSelectionModel<Post>());
		}
	}

	interface PostProperties extends PropertyAccess<Post>
	{
		ModelKeyProvider<Post> id();

		ValueProvider<Post, Date> date();
		ValueProvider<Post, String> forum();
		ValueProvider<Post, String> subject();
		ValueProvider<Post, String> username();
	}
}
