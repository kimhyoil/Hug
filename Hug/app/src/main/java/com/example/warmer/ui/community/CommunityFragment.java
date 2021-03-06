package com.example.warmer.ui.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.warmer.R;
import com.example.warmer.ui.mypage.MyPage;
import com.example.warmer.ui.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CommunityFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Diary> diary_list;
    private ArrayList<Diary> shared_list;
    private static ArrayList<Diary> my_diary_list;

    DiaryListAdapter diaryListAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        diary_list = new ArrayList<>();
        my_diary_list = new ArrayList<>();
        shared_list = new ArrayList<>();

        // construct request to get all diaries
        JsonArrayRequest diaryRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://192.249.19.252:1380/diaries",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        addJsonToDiaryList(diary_list, my_diary_list, shared_list, response);

                        // attach adapter to list
                        diaryListAdapter = new DiaryListAdapter(shared_list);
                        recyclerView.setAdapter(diaryListAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FAIL", "DIARY LOAD");
                    }
                }
        );

        // add the request to singleton requestQueue
        VolleySingleton.getInstance(getContext()).addToRequestQueue(diaryRequest);
        return view;

    }

    public static void addJsonToDiaryList(
            ArrayList<Diary> diary_list,
            ArrayList<Diary> my_diary_list,
            ArrayList<Diary> shared_list,
            JSONArray jsonArray)
    {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Diary diary = new Diary("", "", "", 0);
                diary.setDate(jsonObject.getString("date"));
                diary.setContents(jsonObject.getString("text"));
                diary.setUid(jsonObject.getString("uid"));
                diary.setShared(jsonObject.getInt(("shared")));
                diary_list.add(diary);

                if(MyPage.getUserId().equals(diary.getUid()))
                    my_diary_list.add(diary);
                if(diary.getShared()==1)
                    shared_list.add(diary);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Diary> getDiaryList() {
        return diary_list;
    }

    public static ArrayList<Diary> getMyDiaryList() {
        return my_diary_list;
    }
}
