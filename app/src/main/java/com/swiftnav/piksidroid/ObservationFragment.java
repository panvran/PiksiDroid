package com.swiftnav.piksidroid;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.swiftnav.sbp.client.SBPCallback;
import com.swiftnav.sbp.client.SBPHandler;
import com.swiftnav.sbp.msg.SBPMessage;
import com.swiftnav.sbp.drivers.SBPDriverUDP;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ObservationFragment extends Fragment {
    static final String TAG = "ObservationFragment";
    static final int[] OBS_MESSAGE_LIST = {
            SBPMessage.SBP_MSG_BASE_POS,
            SBPMessage.SBP_MSG_OBS,
            SBPMessage.SBP_MSG_EPHEMERIS,
            SBPMessage.SBP_MSG_PRINT};
    View view;
    Button obs_button;
    EditText obs_address;

    SBPDriverUDP driver;
    SBPHandler piksiHandler;
    SBPHandler handler;

    public ObservationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_observation, container, false);

        piksiHandler = ((MainActivity)getActivity()).handler;
        obs_button = (Button) view.findViewById(R.id.obs_connect);
        obs_address = (EditText) view.findViewById(R.id.obs_address);
        obs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver = new SBPDriverUDP(obs_address.getText().toString());
                handler = new SBPHandler(driver);
                handler.add_callback(OBS_MESSAGE_LIST, new SBPCallback() {
                    @Override
                    public void receiveCallback(SBPMessage msg) {
                        try {
                            piksiHandler.send(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        return view;
    }

    public void connectPiksi(SBPHandler handler_) {
        piksiHandler = handler_;
        piksiHandler.add_callback(OBS_MESSAGE_LIST, new SBPCallback() {
            @Override
            public void receiveCallback(SBPMessage msg) {
                if (handler != null)
                    try {
                        handler.send(msg);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to send observation on UDP: " + e.toString());
                        e.printStackTrace();
                    }
            }
        });
    }

}