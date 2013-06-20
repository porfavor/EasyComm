package com.coffeebean.easycomm;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import com.coffeebean.easycomm.R;
import com.coffeebean.easycomm.util.XmppTool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRoster extends Activity implements OnChildClickListener {

	/** Called when the activity is first created. */

	// 定义两个List用来控制Group和Child中的String;

	private ArrayList<String> groupArray;// 组列表
	private ArrayList<ArrayList<String>> childArray;// 子列表
	private ExpandableListView expandableListView;
	private String pUSERID, pCHATTER;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		
		this.pUSERID = getIntent().getStringExtra("USERID");
        setContentView(R.layout.expandablelistview);   
        
        groupArray = new ArrayList<String>();
        childArray = new ArrayList<ArrayList<String>>();
        expandableListView =(ExpandableListView)findViewById(R.id.expandableListView);   
        
        Roster roster = XmppTool.getConnection().getRoster();
        
        //init group
        Collection<RosterGroup> groups = roster.getGroups();
        Collection<RosterEntry> entries;
        for (RosterGroup group : groups) {
        	Log.i("ActivityRoster", "Group:" + group.getName());
            groupArray.add(group.getName());
            
            entries = group.getEntries();
            ArrayList<String> temp = new ArrayList<String>();
            for (RosterEntry entry : entries) {
            	Log.i("ActivityRoster", "Entry:" + entry.getName());
            	temp.add(entry.getName());
            }
            
            childArray.add(temp);
        }
        
        //entries = roster.getEntries();
        
//        ArrayList<String> groupList = new ArrayList<String>();
//        for (int i = 0; i < 3; i++) {
//            groupList.add("title");
//        }
//        
//        ArrayList<String> itemList1 = new ArrayList<String>();
//        itemList1.add("Item1");
//        itemList1.add("Item2");
//        ArrayList<String> itemList2 = new ArrayList<String>();
//        itemList2.add("Item1");
//        itemList2.add("Item21");
//        itemList2.add("Item3");
//        ArrayList<String> itemList3 = new ArrayList<String>();
//        itemList3.add("Item1");
//        itemList3.add("Item2");
//        itemList3.add("Item3");
//        itemList3.add("Item4");
//        ArrayList<ArrayList<String>> childList = new ArrayList<ArrayList<String>>();
//        childList.add(itemList1);
//        childList.add(itemList2);
//        childList.add(itemList3);
//
//        ExpandableListViewaAdapter adapter = new ExpandableListViewaAdapter(this, groupList, childList);
                   
        ExpandableListViewaAdapter adapter = new ExpandableListViewaAdapter(
        		this, groupArray, childArray
                );   
        expandableListView.setAdapter(adapter); 
        expandableListView.setOnChildClickListener(this); 

	}

	class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
		private Activity activity;
		private ArrayList<String> groupList;
        private ArrayList<ArrayList<String>> childList;
        
        private int selectedGroupPosition = -1;
        private int selectedChildPosition = -1;

        ExpandableListViewaAdapter(Activity a, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
            this.activity = a;
        	this.groupList = groupList;
            this.childList = childList;
        }

        public void setSelectedPosition(int selectedGroupPosition, int selectedChildPosition) {
            this.selectedGroupPosition = selectedGroupPosition;
            this.selectedChildPosition = selectedChildPosition;
        }
        
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null) {
                textView = new TextView(this.activity);
                textView.setPadding(32, 10, 0, 10);
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }

            textView.setText(getChild(groupPosition, childPosition).toString());

            if (groupPosition == selectedGroupPosition) {
                if (childPosition == selectedChildPosition) {
                    textView.setBackgroundColor(0xffb6ddee);
                } else {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    setSelectedPosition(groupPosition, childPosition);
                    notifyDataSetChanged();
                    
                    pCHATTER = (String)getChild(groupPosition, childPosition);
                    //toast(chatter);
                    Log.i("ActivityRoster", "child selected:" + pCHATTER);
                    Intent intent = new Intent(ActivityRoster.this, ActivityChat.class);
                    intent.putExtra("USERID", pUSERID);
					intent.putExtra("CHATTER", pCHATTER);
					startActivity(intent);
					ActivityRoster.this.finish();
                }
            });
            return textView;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LinearLayout cotain = new LinearLayout(this.activity);
            cotain.setPadding(0, 10, 0, 10);
            cotain.setGravity(Gravity.CENTER_VERTICAL);

            ImageView imgIndicator = new ImageView(this.activity);
            TextView textView = new TextView(this.activity);
            textView.setText(getGroup(groupPosition).toString());
            textView.setPadding(5, 0, 0, 0);

            if (isExpanded) {
                //imgIndicator.setBackgroundResource(R.drawable.macro_minus);
            } else {
                //imgIndicator.setBackgroundResource(R.drawable.macro_plus);
            }
            cotain.addView(imgIndicator);
            cotain.addView(textView);
            return cotain;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		toast("点击了");
		return false;
	}

	private void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	} 
}