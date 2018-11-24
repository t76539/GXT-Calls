package ru.calls.client.services;

import ru.calls.shared.Post;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

public interface CallsServiceAsync
{
	void getPosts(PagingLoadConfig config, AsyncCallback<PagingLoadResult<Post>> callback);
}
