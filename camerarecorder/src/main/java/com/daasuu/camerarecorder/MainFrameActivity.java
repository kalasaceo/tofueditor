package com.daasuu.camerarecorder;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kalasa.library.R;

import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.daasuu.camerarecorder.layout.straight.StraightLayoutHelper;
import com.xiaopo.flying.poiphoto.GetAllPhotoTask;
import com.xiaopo.flying.poiphoto.PhotoManager;
import com.xiaopo.flying.poiphoto.datatype.Photo;
import com.xiaopo.flying.poiphoto.ui.adapter.PhotoAdapter;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
public class MainFrameActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final int ADD_FRAME_IMAGE_CODE =2901 ;
  List<String> apaths = new ArrayList<String>();
  private RecyclerView photoList;
  private RecyclerView puzzleList;
  String thisfpath="empty";
  private PuzzleAdapter puzzleAdapter;
  private PhotoAdapter photoAdapter;

  private List<Bitmap> bitmaps = new ArrayList<>();
  private ArrayMap<String, Bitmap> arrayBitmaps = new ArrayMap<>();
  private ArrayList<String> selectedPath = new ArrayList<>();

  private PuzzleHandler puzzleHandler;

  private List<Target> targets = new ArrayList<>();

  private int deviceWidth;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.avtivity_frame);
    puzzleHandler = new PuzzleHandler(this);
    apaths.add("empty");
    deviceWidth = getResources().getDisplayMetrics().widthPixels;



    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    initView();
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
      }, 110);
    } else {
      loadPhoto();
    }
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ADD_FRAME_IMAGE_CODE && resultCode == RESULT_OK) {
      if (data.hasExtra("fpath")) {
        thisfpath=data.getExtras().getString("fpath");
        finish();
      }
    }
  }
  private void loadPhoto() {
    new GetAllPhotoTask() {
      @Override protected void onPostExecute(List<Photo> photos) {
        super.onPostExecute(photos);
        photoAdapter.refreshData(photos);
      }
    }.execute(new PhotoManager(this));
  }

  private void initView() {
    photoList = (RecyclerView) findViewById(R.id.photo_list);
    puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

    photoAdapter = new PhotoAdapter();
    photoAdapter.setMaxCount(5);
    photoAdapter.setSelectedResId(R.drawable.photo_selected_shadow);

    photoList.setAdapter(photoAdapter);
    photoList.setLayoutManager(new GridLayoutManager(this, 4));

    puzzleAdapter = new PuzzleAdapter();
    puzzleList.setAdapter(puzzleAdapter);
    puzzleList.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    puzzleList.setHasFixedSize(true);

    puzzleAdapter.setOnItemClickListener(new PuzzleAdapter.OnItemClickListener() {
      @Override public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {
        Intent intent = new Intent(MainFrameActivity.this, ProcessActivity.class);
        intent.putStringArrayListExtra("photo_path", selectedPath);
        if (puzzleLayout instanceof SlantPuzzleLayout) {
          intent.putExtra("type", 0);
        } else {
          intent.putExtra("type", 1);
        }
        intent.putExtra("piece_size", selectedPath.size());
        intent.putExtra("theme_id", themeId);
        startActivityForResult(intent, ADD_FRAME_IMAGE_CODE);
      }
    });

    photoAdapter.setOnPhotoSelectedListener(new PhotoAdapter.OnPhotoSelectedListener() {
      @Override public void onPhotoSelected(final Photo photo, int position) {
        Message message = Message.obtain();
        message.what = 120;
        message.obj = photo.getPath();
        puzzleHandler.sendMessage(message);
        apaths.add(photo.getPath());
        Picasso.with(MainFrameActivity.this)
            .load("file:///" + photo.getPath())
            .resize(deviceWidth, deviceWidth)
            .centerInside()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .fetch();
      }
    });

    photoAdapter.setOnPhotoUnSelectedListener(new PhotoAdapter.OnPhotoUnSelectedListener() {
      @Override public void onPhotoUnSelected(Photo photo, int position) {
        Bitmap bitmap = arrayBitmaps.remove(photo.getPath());
        bitmaps.remove(bitmap);
        selectedPath.remove(photo.getPath());
        apaths.remove(photo.getPath());
        puzzleAdapter.refreshData(StraightLayoutHelper.getAllThemeLayout(bitmaps.size()), bitmaps);
      }
    });

    photoAdapter.setOnSelectedMaxListener(new PhotoAdapter.OnSelectedMaxListener() {
      @Override public void onSelectedMax() {
        Toast.makeText(MainFrameActivity.this, "maximum pics already selected", Toast.LENGTH_SHORT).show();
      }
    });

    ImageView btnCancel = (ImageView) findViewById(R.id.btn_cancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (bitmaps == null || bitmaps.size() == 0) {
          onBackPressed();
          return;
        }

        arrayBitmaps.clear();
        bitmaps.clear();
        selectedPath.clear();

        photoAdapter.reset();
        puzzleHandler.sendEmptyMessage(119);
      }
    });
    /*ImageView btnMore = (ImageView) findViewById(R.id.btn_more);
    btnMore.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        //showMoreDialog(view);
      }
    });*/
  }
  /*private void showMoreDialog(View view) {
    PopupMenu popupMenu = new PopupMenu(this, view, Gravity.BOTTOM);
    popupMenu.inflate(R.menu.menu_main);
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_playground) {
          Intent intent = new Intent(MainFrameActivity.this, PlaygroundActivity.class);
          startActivity(intent);
        } else if (itemId == R.id.action_about) {
          showAboutInfo();
        }
        return false;
      }
    });
    popupMenu.show();
  }

  private void showAboutInfo() {
    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
    bottomSheetDialog.setContentView(R.layout.about_info);
    bottomSheetDialog.show();
  }*/

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 110
        && grantResults[0] == PackageManager.PERMISSION_GRANTED
        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
      loadPhoto();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    arrayBitmaps.clear();
    arrayBitmaps = null;

    bitmaps.clear();
    bitmaps = null;
  }

  private void refreshLayout() {
    puzzleList.post(new Runnable() {
      @Override public void run() {
        puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
      }
    });
  }

  public void fetchBitmap(final String path) {
    Log.d(TAG, "fetchBitmap: ");
    final Target target = new Target() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        Log.d(TAG, "onBitmapLoaded: ");

        arrayBitmaps.put(path, bitmap);
        bitmaps.add(bitmap);
        selectedPath.add(path);

        puzzleHandler.sendEmptyMessage(119);
        targets.remove(this);
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {

      }

      @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

      }
    };

    Picasso.with(this)
        .load("file:///" + path)
        .resize(300, 300)
        .centerInside()
        .config(Bitmap.Config.RGB_565)
        .into(target);

    targets.add(target);
  }
  @Override
  public void finish() {
    String listString = "empty";
    for (String s : apaths)
    {
      listString += s + "*";
    }
    Intent data = new Intent();
    String path=listString+"--"+thisfpath;
    data.putExtra("framepath", path);
    this.setResult(RESULT_OK, data);
    super.finish();
  }
  private static class PuzzleHandler extends Handler {
    private WeakReference<MainFrameActivity> mReference;

    PuzzleHandler(MainFrameActivity activity) {
      mReference = new WeakReference<>(activity);
    }

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 119) {
        mReference.get().refreshLayout();
      } else if (msg.what == 120) {
        mReference.get().fetchBitmap((String) msg.obj);
      }
    }
  }
}
