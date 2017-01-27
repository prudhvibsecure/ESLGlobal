package eslglobal.com.esl.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Vector;

import eslglobal.com.esl.Bsecure;
import eslglobal.com.esl.R;
import eslglobal.com.esl.adapter.FolderAdapter;
import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.common.Item;
import eslglobal.com.esl.helper.RecyclerOnScrollListener;
import eslglobal.com.esl.tasks.HTTPPostTask;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class FoldersFragments extends ParentFragment implements IItemHandler {

	private View layout = null;

	private Bsecure bsecure = null;

	private FolderAdapter adapter = null;

	private RecyclerView mRecyclerView = null;

	private SwipeRefreshLayout mSwipeRefreshLayout = null;

	private RecyclerOnScrollListener recycScollListener = null;

	private boolean isRefresh = false;

	private String total_pages = "0";

	private int tempId = -1;

	private String fid = "";

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		this.bsecure = (Bsecure) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		layout = inflater.inflate(R.layout.tempalte_recyclerview, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.green,
				R.color.red, R.color.bs_blue, R.color.colorPrimaryDark);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				recycScollListener.resetValue();
				getData(1, 0);
			}
		});

		((TextView) layout.findViewById(R.id.catgr_txt)).setText(R.string.nofolder);
		layout.findViewById(R.id.fab_newmail).setVisibility(View.GONE);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LinearLayoutManager layoutManager = new LinearLayoutManager(bsecure);

		mRecyclerView = (RecyclerView) layout.findViewById(R.id.content_list);

		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		recycScollListener = new RecyclerOnScrollListener(layoutManager) {

			@Override
			public void onLoadMoreData(int currentPage) {
				if (total_pages.length() > 0)
					if (currentPage <= Integer.parseInt(total_pages) - 1) {
						getData(2, currentPage);
						getView().findViewById(R.id.catgr_pbar).setVisibility(View.VISIBLE);

					}
			}
		};

		mRecyclerView.addOnScrollListener(recycScollListener);
		getData(1, 0);

		setHasOptionsMenu(true);
		getActivity().supportInvalidateOptionsMenu();
	}

	public void refresh() {
		isRefresh = true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!bsecure.isDrawerOpen())
			inflater.inflate(R.menu.inbox_menu, menu);
		MenuItem item = menu.findItem(R.id.menu_search);
		item.setVisible(false);
		menu.findItem(R.id.cm_in_folder).setVisible(false);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		if (tempId == 1) {

			MenuItem item = menu.findItem(R.id.cm_delete);
			item.setVisible(false);

			MenuItem cm_delete_done = menu.findItem(R.id.cm_delete_done);
			cm_delete_done.setVisible(true);

		} else if (tempId == 2) {

			MenuItem item = menu.findItem(R.id.cm_delete);
			if (item != null)
				item.setVisible(true);

			MenuItem cm_delete_done = menu.findItem(R.id.cm_delete_done);
			if (cm_delete_done != null)
				cm_delete_done.setVisible(false);

		}

		super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			bsecure.unLockMode();
			adapter.showCheckBoxs(false);
			adapter.notifyDataSetChanged();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 2;

			break;

		case R.id.cm_delete:

			adapter.showCheckBoxs(true);
			adapter.notifyDataSetChanged();
			bsecure.lockMode();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 1;

			break;

		case R.id.cm_delete_done:

			removeFolder();

			bsecure.unLockMode();
			adapter.showCheckBoxs(false);
			adapter.notifyDataSetChanged();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 2;

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void removeFolder() {
		try {
			int count = adapter.getItems().size();

			for (int i = 0; i < count; i++) {
				Item item = adapter.getItems().get(i);
				if (item.getAttribute("checked").equalsIgnoreCase("y")) {
					fid = fid + "," + item.getAttribute("fid");
				}
			}

			if (fid.trim().length() == 0)
				return;

			fid = fid.substring(1);

			String url = AppSettings.getInstance(bsecure).getPropertyValue("delete_folder");
			JSONObject object = new JSONObject();
			object.put("email", bsecure.getFromStore("email"));
			object.put("fid", fid);

			HTTPostJson post = new HTTPostJson(this, getActivity(), object.toString(), 2);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getData(int requestId, int currentNo) {

		try {
			getView().findViewById(R.id.catgr_txt).setVisibility(View.GONE);

			if (adapter == null) {
				adapter = new FolderAdapter(bsecure, "");
				mRecyclerView.setAdapter(adapter);
			}

//			HTTPBackgroundTask task = new HTTPBackgroundTask(this, getActivity(), 1, requestId);
			String link = bsecure.getPropertyValue("view_folder");
			link = link.replace("(EMAIL)", bsecure.getFromStore("email"));	
			HTTPPostTask task = new HTTPPostTask(getActivity(), this);
			task.disableProgress();
			task.userRequest("", requestId, link, "", 1);// object.toString()
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroyView() {

		adapter.clear();
		adapter.notifyDataSetChanged();
		adapter.release();
		adapter = null;

		mRecyclerView.removeAllViews();
		mRecyclerView = null;

		layout = null;

		super.onDestroyView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden)
			if (isRefresh) {
				isRefresh = false;
				getData(1, 0);
			}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {

		layout = null;

		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onFinish(Object results, int requestType) {

		try {

			getView().findViewById(R.id.catgr_pbar).setVisibility(View.GONE);

			switch (requestType) {

			case 1:

				if (results != null) {

					Item item = (Item) results;
					Vector<Item> items = (Vector<Item>) item.get("folder_detail");
					if (items != null && items.size() > 0) {
						adapter.setItems(items);
						adapter.notifyDataSetChanged();
						return;
					}

				}

				adapter.clear();
				adapter.notifyDataSetChanged();
				getView().findViewById(R.id.catgr_txt).setVisibility(View.VISIBLE);

				break;
			case 2:
				Utils.dismissProgress();
				parseDeleteFolder(results);
				fid="";
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseDeleteFolder(Object object) throws Exception {
		if (object != null) {
			JSONObject jsonObject = new JSONObject(object.toString());
			if (jsonObject.optString("status").equalsIgnoreCase("0")) {

				String temp[] = fid.split(",");

				for (int i = 0; i < temp.length; i++) {
					adapter.removeItem(temp[i]);
				}
				adapter.notifyDataSetChanged();

			}
			bsecure.showToast(jsonObject.optString("statusdescription"));
		}

	}

	@Override
	public void onError(String errorCode, int requestType) {

		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(true);

		bsecure.showToast(errorCode);

		getView().findViewById(R.id.catgr_pbar).setVisibility(View.GONE);
	}

	@Override
	public String getFragmentName() {
		return "Folders";
	}

	@Override
	public void onFragmentChildClick(View view) {

		int itemPosition = mRecyclerView.getChildLayoutPosition(view);

		Item item = adapter.getItems().get(itemPosition);

		bsecure.FolderViewMessage(item);

		super.onFragmentChildClick(view);
	}

}
