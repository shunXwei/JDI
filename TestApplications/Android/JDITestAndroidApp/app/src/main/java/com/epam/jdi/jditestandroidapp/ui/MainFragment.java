package com.epam.jdi.jditestandroidapp.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.epam.jdi.jditestandroidapp.MainItem;
import com.epam.jdi.jditestandroidapp.R;
import com.epam.jdi.jditestandroidapp.adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView mList;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<MainItem> list = new ArrayList<>();
        list.add(new MainItem(R.string.menu_contact_form, R.id.contact_form));
        list.add(new MainItem(R.string.date_form, R.id.date_form));


        mList = (ListView) view.findViewById(android.R.id.list);

        mList.setAdapter(new ItemAdapter(getContext(), list));
        mList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        if (id == R.id.contact_form) {
            ft.replace(R.id.conatiner, ContactFormFragment.newInstance());
        } else if (id == R.id.date_form) {
            ft.replace(R.id.conatiner, ServiceDatesFragment.newInstance());
        }

        ft.commit();
    }

    public static Fragment newInstance() {
        return new MainFragment();
    }
}
