package com.wathsumit.mobility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ViewGroup;

import com.wathsumit.mobility.databinding.ActivityMainBinding;
import com.wathsumit.mobility.databinding.ItemIamgeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    ActivityMainBinding mBinding;
    ProgressDialog pDialog;
    private ArrayList<ImageModel> mImageModelList = new ArrayList<>();
    private HashMap<Integer, Bitmap> photoThumbnails = new HashMap<>();
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_main);
        mBinding.recyclerMain.setLayoutManager(new GridLayoutManager(this, 2,
                LinearLayoutManager.VERTICAL, false));
        new AsyncListViewLoader().execute("https://picsum.photos/list");

    }

    public void refreshList() {
        if (mImageModelList.size() == 0) {
            //mBinding.tvNoService.setVisibility(View.VISIBLE);
        } else {
            // mBinding.tvNoService.setVisibility(View.GONE);
            mBinding.recyclerMain.setAdapter(new RecyclerAdapter<ImageModel, ImageModelHolder>(this, mImageModelList) {
                @NonNull
                @Override
                public ImageModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new ImageModelHolder(ItemIamgeBinding.inflate(getInflater(), parent, false));
                }

                @Override
                public void onBindViewHolder(@NonNull ImageModelHolder holder, int position) {
                    holder.bind(getItem(position));
                }
            });
        }
    }

    private void showProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    class ImageModelHolder extends RecyclerView.ViewHolder {
        ItemIamgeBinding binding;
        ImageModel imageModel;

        ImageModelHolder(ItemIamgeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ImageModel imageModel) {
            this.imageModel = imageModel;
            //binding.imgAuthImage.setImageBitmap(imageModel.getBtmap());
            binding.tvAuthName.setText(imageModel.getAuthor());

            String url1 = "https://picsum.photos/300/300?image=" + imageModel.getId();
            // new DownloadImageTask(MainActivity.this, binding).execute(url1);
            Bitmap thumbnail = photoThumbnails.get(imageModel.getId());
            if (thumbnail == null) {
                // Image was not found in cache; load it from the server

                new DownloadImageTask(MainActivity.this, imageModel).execute(url1);

            } else {
                binding.imgAuthImage.setImageBitmap(thumbnail);
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Context mContext;
        ImageModel mImageModel;

        public DownloadImageTask(Context context, ImageModel imageModel) {
            mContext = context;
            mImageModel = imageModel;
        }

        public void onPreExecute() {
            showProgressDialog();
        }

        public void onPostExecute(Bitmap result) {
            dismissProgressDialog();
            if (result != null) {
                photoThumbnails.put(mImageModel.getId(), result);

            }
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private class AsyncListViewLoader extends AsyncTask<String, Void, ArrayList<ImageModel>> {


        @Override
        protected void onPostExecute(ArrayList<ImageModel> result) {
            super.onPostExecute(result);
            mImageModelList = result;
            dismissProgressDialog();
            refreshList();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected ArrayList<ImageModel> doInBackground(String... params) {

            ArrayList<ImageModel> result = new ArrayList<>();
            String inline = "";
            try {
                URL u = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                Scanner sc = new Scanner(u.openStream());
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                System.out.println("\nJSON data in string format");
                System.out.println(inline);
                sc.close();

                JSONArray arr = new JSONArray(inline);
                for (int i = 0; i < arr.length(); i++) {
                    result.add(convertResult(arr.getJSONObject(i)));
                }

                return result;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        private ImageModel convertResult(JSONObject obj) throws JSONException {
            int id = obj.getInt("id");
            String author = obj.getString("author");
            return new ImageModel(id, author);
        }

    }

}



