package com.codingPower.framework.ui.support;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.codingPower.framework.net.RemoteService;
import com.codingPower.framework.net.Response;
import com.codingPower.framework.worker.NetWorker;

/**
 * list相关类的封装
 * @author pengfan
 *
 */
public class ListSupport {

    public static abstract class NetWorkListFragment<E> extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<E>> {
        BaseArrayAdapter<E> mAdapter;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = createAdapter();
            setListAdapter(mAdapter);
            setListShown(false);
            getLoaderManager().initLoader(0, null, this);
        }

        public BaseArrayAdapter<E> getAdapter() {
            return mAdapter;
        }

        protected abstract BaseArrayAdapter<E> createAdapter();

        @Override
        public void onLoadFinished(Loader<List<E>> loader, List<E> data) {
            // Set the new data in the adapter.
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
    }

    public static abstract class BaseArrayAdapter<E> extends ArrayAdapter<E> {

        protected final LayoutInflater mInflater;

        protected BaseArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<E> data) {
            clear();
            if (data != null) {
                for (E entry : data) {
                    add(entry);
                }
            }
        }

    }

    public static abstract class NetworkListLoader<E> extends AsyncTaskLoader<List<E>> {
        NetWorker networker;
        List<E> list;
        RemoteService service;

        public NetworkListLoader(Context context, RemoteService service) {
            super(context);
            networker = new NetWorker(context);
            this.service = service;
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<E> loadInBackground() {
            return handleResponse(networker.directRequest(service));
        }

        public abstract List<E> handleResponse(Response response);

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<E> res) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (list != null) {
                    onReleaseResources(res);
                }
            }
            List<E> oldlist = list;
            list = res;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(list);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldlist != null) {
                onReleaseResources(oldlist);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (list != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(list);
            }

            if (takeContentChanged() || list == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<E> res) {
            super.onCanceled(res);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(res);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (list != null) {
                onReleaseResources(list);
                list = null;
            }

        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<E> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }

}
