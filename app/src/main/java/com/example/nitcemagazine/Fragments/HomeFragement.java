package com.example.nitcemagazine.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nitcemagazine.FragmentAdapters.HomeAdapter;
import com.example.nitcemagazine.FragmentAdapters.ModelClass;
import com.example.nitcemagazine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragement extends Fragment {
    List<ModelClass> articleList;
    HomeAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    ModelClass modelClass = new ModelClass();
    SearchView searchView;
    List<String > authorName ;
//    String authorName=new String();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragement,null);

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setBackgroundColor(0xDDDDDD);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        articleList = new ArrayList<>();

        getArticle();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    void getArticle()
    {
        reference.child("PostedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                modelClass = snapshot.getValue(ModelClass.class);
//                String cat = snapshot.child("category").getValue().toString();
                if(modelClass.getCategory().equalsIgnoreCase("home")) {
                    articleList.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                    searchView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new HomeAdapter(articleList, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar,menu);
        MenuItem item=menu.findItem(R.id.search_bar);
        searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() == 0)
                    getArticle();
                else
                    txtSearch(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private void txtSearch(String str){
        List<ModelClass> articleList2=new ArrayList<>();

        authorName = new ArrayList<>();
        reference.child("PostedArticle").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String cat = snapshot.child("category").getValue().toString();
                //
                String auid=snapshot.child("authorUid").getValue().toString();


                reference.child("UserType").child(auid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        String usertype = snapshot1.getValue().toString();
                        reference.child(usertype).child(auid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

//                                authorName.add(snapshot.child("name").getValue().toString());

                                modelClass = snapshot.getValue(ModelClass.class);

                                if(modelClass.getCategory().equalsIgnoreCase("home") && (snapshot2.child("name").getValue().toString().toLowerCase().contains(str.toLowerCase()))) {
                                    System.out.println(modelClass.getTitle());

                                    articleList2.add(modelClass);
                                    modelClass.setId(snapshot.getKey());
                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                modelClass = snapshot.getValue(ModelClass.class);

                //
                if(modelClass.getCategory().equalsIgnoreCase("home") && (modelClass.getTitle().toLowerCase().contains(str.toLowerCase()))) {
                    System.out.println(modelClass.getTitle());
                    articleList2.add(modelClass);
                    modelClass.setId(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new HomeAdapter(articleList2, getContext());
        recyclerView.setAdapter(adapter);
    }

    public void removeSearchBar()
    {
        searchView.setVisibility(View.GONE);
    }
}
//

//
//        reference.child("PostedArticle").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshotx) {
//
//                for (DataSnapshot snapshot : snapshotx.getChildren())
//                {
////                String cat = snapshot.child("category").getValue().toString();
//                //
//                String auid = snapshot.child("authorUid").getValue().toString();
//
//
//                reference.child("UserType").child(auid).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
//                        String usertype = snapshot1.getValue().toString();
//                        reference.child(usertype).child(auid).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
//
////                                authorName.add(snapshot.child("name").getValue().toString());
//
//                                modelClass = snapshot.getValue(ModelClass.class);
//
//                                if (modelClass.getCategory().equalsIgnoreCase("home"))// && (snapshot2.child("name").getValue().toString().toLowerCase().contains(str.toLowerCase())))
//                                     {
//                                    System.out.println(modelClass.getCategory() +" " + modelClass.getTitle());
//
//                                    articleList2.add(modelClass);
//                                    modelClass.setId(snapshot.getKey());
//                                    adapter.notifyDataSetChanged();
//                                         adapter = new HomeAdapter(articleList2, getContext());
//                                         recyclerView.setAdapter(adapter);
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                //
//                if (modelClass.getCategory().equalsIgnoreCase("home") && (modelClass.getTitle().toLowerCase().contains(str.toLowerCase()))) {
//                    System.out.println(modelClass.getTitle());
//                    articleList2.add(modelClass);
//                    modelClass.setId(snapshot.getKey());
//                    adapter.notifyDataSetChanged();
//                    adapter = new HomeAdapter(articleList2, getContext());
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });