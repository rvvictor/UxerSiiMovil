package com.example.uxersiipmchido.ui.Buscar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uxersiipmchido.R;
import com.example.uxersiipmchido.databinding.FragmentSlideshowBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SlideshowFragment extends Fragment {

    ImageView scan, search;
    EditText qrval;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
    View view= inflater.inflate(R.layout.fragment_slideshow, container, false);
    qrval=view.findViewById(R.id.qrcode);
    scan=view.findViewById(R.id.escaner);
    scan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            escanearQR();
        }
    });
    search=view.findViewById(R.id.buscar);
    search.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buscarCode();
        }
    });
    return view;
    }
    private void buscarCode(){

    }
    private void escanearQR(){
        IntentIntegrator integrator = new IntentIntegrator(requireActivity());
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show();
                } else {
                    String qrCodeValue = result.getContents();
                    qrval.setText(qrCodeValue);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}