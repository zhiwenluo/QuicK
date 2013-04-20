/**
 * Android File Chooser
 * Copyright (C) 2012  ScR4tCh
 * Contact scr4tch@scr4tch.org
 *
 * This is a fork of android-file-dialog (com.lamerman) licensed new BSD
 * Take a look at https://code.google.com/p/android-file-dialog/
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package com.uniquestudio.filechooser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uniquestudio.quick.QuicK;
import com.uniquestudio.quick.R;

/**
 * TODO ON FILE CREATION : 
 *
 *	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
 *       + Environment.getExternalStorageDirectory()))); 
 **/

/**
 * Activity for file selection
 * 
 * @author ScR4tCH
 * 
 */
public class FileChooserDialog extends ListActivity {
    // drawables for elements in list
    protected Bitmap folderDrawable;

    protected Bitmap fileDrawable;

    protected Bitmap upDrawable;

    // selection mode (which fs elements should appear as selection ?)
    public static final int SELECT_FOLDER = 2;

    public static final int SELECT_FILE = 4;

    private int selectMode = SELECT_FOLDER | SELECT_FILE;

    private int viewMode = SELECT_FOLDER | SELECT_FILE;

    // allow "browsing"
    private boolean browseEanbled = true;

    // mode of operation (CREATE OR JUST OPEN)
    public static final int MODE_CREATE = 0;

    public static final int MODE_OPEN = 1;

    private int operationMode = MODE_OPEN;

    // list adapter
    private FileChooserDialogAdapter fileAdapter;

    // start path
    private String startPath;

    public static final String START_PATH = "START_PATH";

    public static final String FORMAT_FILTER = "FORMAT_FILTER";

    public static final String RESULT_PATH = "RESULT_PATH";

    public static final String OPERATION_MODE = "OPERATION_MODE";

    public static final String SELECT_MODE = "SELECT_MODE";

    public static final String VIEW_MODE = "VIEW_MODE";

    public static final String BROWSE_ENABLED = "BROWSE_ENABLED";

    public static final String FILE_DRAWABLE = "FILE_DRAWABLE";

    public static final String FOLDER_DRAWABLE = "FOLDER_DRAWABLE";

    public static final String UP_DRAWABLE = "UP_DRAWABLE";

    // view
    private TextView myPath;

    private EditText mFileName;

    private Button selectButton;

    private LinearLayout layoutSelect;

    private LinearLayout layoutCreate;

    private InputMethodManager inputManager;

    private FileFilter ff;

    private File selectedFile;

    private boolean isFirstStage = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: optional ?
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
        // set default drawables
        folderDrawable = BitmapFactory.decodeResource(getResources(),
                R.drawable.folder);
        fileDrawable = BitmapFactory.decodeResource(getResources(),
                R.drawable.file);
        upDrawable = BitmapFactory.decodeResource(getResources(),
                R.drawable.folder);

        setResult(RESULT_CANCELED, getIntent());

        setContentView(R.layout.file_dialog_main);
        myPath = (TextView) findViewById(R.id.path);
        mFileName = (EditText) findViewById(R.id.fdEditTextFile);

        inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        selectButton = (Button) findViewById(R.id.fdButtonSelect);

        final Button newButton = (Button) findViewById(R.id.fdButtonNew);
        newButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setCreateVisible(v);

                mFileName.setText("");
                mFileName.requestFocus();
            }
        });

        // get "settings" from Intent
        Intent i = getIntent();

        operationMode = i.getIntExtra(OPERATION_MODE, MODE_OPEN);
        viewMode = i.getIntExtra(VIEW_MODE, viewMode);
        selectMode = i.getIntExtra(SELECT_MODE, selectMode);

        browseEanbled = i.getBooleanExtra(BROWSE_ENABLED, browseEanbled);

        if (i.getParcelableExtra(FORMAT_FILTER) instanceof FileFilter)
            ff = (FileFilter) i.getParcelableExtra(FORMAT_FILTER);

        selectMode = i.getIntExtra(SELECT_MODE, SELECT_FOLDER | SELECT_FILE);
        viewMode = i.getIntExtra(VIEW_MODE, SELECT_FOLDER | SELECT_FILE);

        if (i.getParcelableExtra(FOLDER_DRAWABLE) instanceof Bitmap)
            folderDrawable = (Bitmap) (i.getParcelableExtra(FOLDER_DRAWABLE));

        if (i.getParcelableExtra(FILE_DRAWABLE) instanceof Bitmap)
            fileDrawable = (Bitmap) (i.getParcelableExtra(FILE_DRAWABLE));

        if (i.getParcelableExtra(UP_DRAWABLE) instanceof Bitmap)
            upDrawable = (Bitmap) (i.getParcelableExtra(UP_DRAWABLE));

        // TODO: add other view specific settings as fontcolor
        // "style" for not readable
        // and so on ...

        // hide new button in "open" mode
        if (operationMode == MODE_OPEN) {
            newButton.setVisibility(View.GONE);
        }

        layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);
        layoutCreate = (LinearLayout) findViewById(R.id.fdLinearLayoutCreate);
        layoutCreate.setVisibility(View.GONE);

        startPath = getIntent().getStringExtra(START_PATH);
        startPath = startPath != null ? startPath
                : FileChooserDialogAdapter.ROOT;

        File file = new File(startPath);
        selectedFile = file;

        if (selectedFile.isDirectory()
                && (selectMode & SELECT_FOLDER) == SELECT_FOLDER)
            selectButton.setEnabled(true);

        fileAdapter = new FileChooserDialogAdapter(this, startPath, viewMode,
                browseEanbled, ff);
        getListView().setAdapter(fileAdapter);

        myPath.setText(getText(R.string.location) + ": "
                + fileAdapter.getCurrentPath());

        // 设置上下文菜单
        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = (File) fileAdapter.getItem(position);

        if (file == null)
            return;

        setSelectVisible(v);

        if (file.isDirectory()) {
            selectButton.setEnabled(false);
            if (file.canRead()) {
                fileAdapter.putLastPositions(fileAdapter.getCurrentPath(),
                        position);
                fileAdapter.update(file.getAbsolutePath());

                myPath.post(new Runnable() {
                    public void run() {
                        myPath.setText(getText(R.string.location) + ": "
                                + fileAdapter.getCurrentPath());
                    }
                });

                if ((selectMode & SELECT_FOLDER) == SELECT_FOLDER) {
                    selectedFile = file;
                    // fileAdapter.setSelected(position);
                    // selectButton.setEnabled(true);
                }
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(
                                "[" + file.getName() + "] "
                                        + getText(R.string.cant_read_folder))
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {

                                    }
                                }).show();
            }
        } else {
            selectedFile = file;
            // 调用系统级别媒体打开图片--视频--录音
            String fileSuffixName = getFileNameNoEx(selectedFile.toString());
            Log.e("ffffffffff", fileSuffixName);
            Uri mUri = Uri.parse("file://" + selectedFile.toString());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (fileSuffixName.equals(".jpeg")) {
                intent.setDataAndType(mUri, "image/*");
            } else if (fileSuffixName.equals(".3gp")) {
                intent.setDataAndType(mUri, "video/*");
            }
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            selectButton.setEnabled(false);

            if (layoutCreate.getVisibility() == View.VISIBLE) {
                layoutCreate.setVisibility(View.GONE);
                layoutSelect.setVisibility(View.VISIBLE);
            } else {
                if (!fileAdapter.getCurrentPath().equals(startPath)) {
                    fileAdapter.updateToParentPath();
                } else {
                    return super.onKeyDown(keyCode, event);
                }
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "delete");
        menu.add(0, 2, 0, "rename");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        File file = (File) fileAdapter.getItem((int) info.id);
        Log.e("ffffff", file.toString());
        switch (item.getItemId()) {
        case 1:
            deleteFile(file);

            break;

        case 2:
            if (file.isDirectory()) {
                Toast.makeText(FileChooserDialog.this, "Illegal operation..",
                        Toast.LENGTH_SHORT).show();
            } else {
                renameByFile(file);
            }
            break;
        default:
            break;
        }
        return super.onContextItemSelected(item);
    }

    // 删除文件
    private void deleteFile(final File file) {
        this.confirmDialog("删除文件", "确定要删除文件吗？ ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 确定删除
                        try {
                            // 删除文件
                            boolean isDir = file.isDirectory();
                            deleteAll(file);
                            // 删除成功，则弹出提示
                            Toast.makeText(FileChooserDialog.this,
                                    "Delete successfully", Toast.LENGTH_SHORT)
                                    .show();
                            // 删除成功，则刷新当前目录
                            if (isDir) {
                                fileAdapter
                                        .updatefiles(QuicK.STORAGE_LOCATION);
                            } else {
                                fileAdapter.update(file.getParentFile() + "/");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            // 删除失败，则弹出失败对话框
                            Toast.makeText(FileChooserDialog.this,
                                    "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    /**
     * Define se o botao de CREATE e visivel.
     * 
     * @param v
     */
    private void setCreateVisible(View v) {
        layoutCreate.setVisibility(View.VISIBLE);
        layoutSelect.setVisibility(View.GONE);

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        selectButton.setEnabled(false);
    }

    private void setSelectVisible(View v) {
        layoutCreate.setVisibility(View.GONE);
        // layoutSelect.setVisibility(View.VISIBLE);

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        selectButton.setEnabled(false);
    }

    // 获取文件名后缀
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(dot, filename.length());
            }
        }
        return filename;
    }

    /**
     * 递归删除文件
     * 
     * @param file
     *            要删除的文件或者文件夹
     * @throws IOException
     *             文件找不到或者删除错误的时候抛出
     * */
    public static void deleteAll(File file) throws IOException {
        // 文件夹不存在不存在
        if (!file.exists()) {
            throw new IOException("指定目录不存在:" + file.getName());
        }
        boolean rslt = true;// 保存中间结果
        if (!(rslt = file.delete())) {// 先尝试直接删除
            // 若文件夹非空。枚举、递归删除里面内容
            File subs[] = file.listFiles();
            for (int i = 0; i <= subs.length - 1; i++) {
                if (subs[i].isDirectory())
                    deleteAll(subs[i]);// 递归删除子文件夹内容
                rslt = subs[i].delete();// 删除子文件夹本身
            }
            rslt = file.delete();// 删除此文件夹本身
        }
        if (!rslt) {
            throw new IOException("无法删除:" + file.getName());
        }
        return;
    }

    /**
     * 重命名操作
     * 
     * @param file
     *            需要重命名的文件
     */
    public void renameByFile(final File file) {
        // 创建布局对象
        final DialogLayout layout = new DialogLayout(this);
        // 设置初始化的值
        layout.getMessageTextView().setText("Please enter the new name:");
        layout.getInputEditText().setText(file.getName());
        this.showCustomDialog("rename", layout,
        // 点击确定时
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 获取新设置的名称
                        String newName = layout.getInputEditText().getText()
                                .toString();
                        // 判断新名称是否与旧名称一样，
                        // 如果不一样判断新名词是否已经存在于当前目录了
                        if (!newName.equals(file.getName())) {

                            final String allName = file.getParentFile() + "/"
                                    + newName;
                            // 判断是否重名
                            if (new File(allName).exists()) {
                                // 弹出对话框判断是否覆盖
                                confirmDialog(
                                        "重命名",
                                        "文件名重复，是否需要覆盖？",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // 重命名操作
                                                boolean flag = file
                                                        .renameTo(new File(
                                                                allName));
                                                if (flag == true) {
                                                    // 重命名之后，刷新
                                                    Toast.makeText(
                                                            FileChooserDialog.this,
                                                            "Rename successfully",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                    // 重命名成功，则刷新当前目录
                                                    fileAdapter.update(file
                                                            .getParentFile()
                                                            + "/");
                                                } else {
                                                    // 提示失败
                                                    Toast.makeText(
                                                            FileChooserDialog.this,
                                                            "Rename failed",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.cancel();
                                            }
                                        });

                            } else {
                                boolean flag = file.renameTo(new File(allName));
                                if (flag == true) {
                                    // 重命名之后提示
                                    Toast.makeText(FileChooserDialog.this,
                                            "Rename successfully",
                                            Toast.LENGTH_SHORT).show();
                                    // 重命名成功，则刷新当前目录
                                    fileAdapter.update(file.getParentFile()
                                            + "/");
                                } else {
                                    // 提示失败
                                    Toast.makeText(FileChooserDialog.this,
                                            "Rename failed", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                }, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }, new DialogInterface.OnCancelListener() {

                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();

                    }
                }

        );
    }

    private void confirmDialog(String title, String message,
            DialogInterface.OnClickListener positiveButtonEventHandle,
            DialogInterface.OnClickListener negativeButtonEventHandle) {
        Builder builder = new Builder(FileChooserDialog.this);
        builder.setTitle(title);
        builder.setMessage(message);
        // 确定按钮的监听事件
        builder.setPositiveButton(android.R.string.ok,
                positiveButtonEventHandle);
        // 取消按钮的事件监听
        builder.setNegativeButton(android.R.string.cancel,
                negativeButtonEventHandle);
        builder.setCancelable(true);
        builder.create().show();
    }

    private void showCustomDialog(String title, View dialogview,
            DialogInterface.OnClickListener positiveButtonEventHandle,
            DialogInterface.OnClickListener negativeButtonEventHandle,
            DialogInterface.OnCancelListener cancelButtonEventHandle) {
        Builder builder = new Builder(FileChooserDialog.this);
        builder.setTitle(title);
        builder.setView(dialogview);
        // 确定按钮的监听事件
        builder.setPositiveButton(android.R.string.ok,
                positiveButtonEventHandle);
        // 取消按钮的事件监听
        builder.setNegativeButton(android.R.string.cancel,
                negativeButtonEventHandle);
        builder.setOnCancelListener(cancelButtonEventHandle);
        builder.create();
        builder.show();
    }

    
    
}
