package ru.calls.client.services;

import ru.calls.shared.Post;

import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
 
@RemoteServiceRelativePath("calls")
public interface CallsService extends RemoteService
{
	PagingLoadResult<Post> getPosts(PagingLoadConfig config);
}
