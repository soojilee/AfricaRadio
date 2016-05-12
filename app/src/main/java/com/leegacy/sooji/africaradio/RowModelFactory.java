package com.leegacy.sooji.africaradio;

import com.leegacy.sooji.africaradio.DataObjects.PlaylistItem;
import com.leegacy.sooji.africaradio.Models.PlaylistRowModel;

/**
 * Created by soo-ji on 16-04-26.
 */
public class RowModelFactory {
    public static PlaylistRowModel getPlaylistRowModel(PlaylistItem play, String audioKey){
        PlaylistRowModel model = new PlaylistRowModel();
        model.setTitle(play.getTitle());
        model.setAudioFile(audioKey);
        model.setNumComments(play.getNumComments());
        model.setNumHearts(play.getNumHearts());
        return model;
    }

//    public static DecodeAudioFile(String audioFile){
//        byte[] decoded = Base64.decode(audioFile, Base64.DEFAULT);
//        File file2 = new File(Environment.getExternalStorageDirectory() + "/hello-5.wav");
//        FileOutputStream os = new FileOutputStream(file2, true);
//        os.write(decoded);
//        os.close();
//    }
}
