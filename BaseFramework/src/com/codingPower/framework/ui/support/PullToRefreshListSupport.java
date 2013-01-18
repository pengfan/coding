package com.codingPower.framework.ui.support;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.codingPower.framework.net.RemoteService;
import com.codingPower.framework.net.Response;
import com.codingPower.framework.ui.support.ListSupport.BaseArrayAdapter;
import com.codingPower.framework.worker.NetWorker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 下拉刷新列表
 * @author pengfan
 *
 */
public class PullToRefreshListSupport {

    public static abstract class NetWorkPullToRrefreshListFragment<E> extends PullToRefreshListFragment implements
            LoaderManager.LoaderCallbacks<List<E>>, OnRefreshListener<ListView> {
        BaseArrayAdapter<E> mAdapter;
        protected PullToRefreshListView mPullRefreshListView;
        private NetWorker netWorker;
        protected List<E> data = new ArrayList<E>();

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Get PullToRefreshListView from Fragment
            mPullRefreshListView = getPullToRefreshListView();
            mPullRefreshListView.setOnRefreshListener(this);

            netWorker = new NetWorker(getActivity());
            // Create an empty adapter we will use to display the loaded data.
            mAdapter = createAdapter();
            setListAdapter(mAdapter);
            setListShown(false);
            getLoaderManager().initLoader(0, null, this);
        }

        public BaseArrayAdapter<E> getAdapter() {
            return mAdapter;
        }

        /**
         * 构造adapter
         * @return
         */
        protected abstract BaseArrayAdapter<E> createAdapter();

        /**
         * 刷新时，请求service
         * @return
         */
        protected abstract RemoteService onRefreshService();

        /**
         * 处理请求回来之后的情况
         * @return
         */
        protected abstract void handleRefreshResponse(Response rsp);

        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            // Do work to refresh the list here.
            new GetDataTask().execute(onRefreshService());
        }

        @Override
        public void onLoadFinished(Loader<List<E>> loader, List<E> data) {
            // Set the new data in the adapter.
            this.data = data;
            mAdapter.setData(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<E>> loader) {
            mAdapter.setData(null);
        }

        private class GetDataTask extends AsyncTask<RemoteService, Void, Response> {

            @Override
            protected Response doInBackground(RemoteService... params) {
                return netWorker.directRequest(params[0]);
            }

            @Override
            protected void onPostExecute(Response rsp) {
                handleRefreshResponse(rsp);
                mAdapter.notifyDataSetChanged();
                // Call onRefreshComplete when the list has been refreshed.
                mPullRefreshListView.onRefreshComplete();
                super.onPostExecute(rsp);
            }
        }
    }

}
