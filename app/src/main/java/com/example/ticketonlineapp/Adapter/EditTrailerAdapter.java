package com.example.ticketonlineapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketonlineapp.Activity.Movie.EditMovieActivity;
import com.example.ticketonlineapp.R;

public class EditTrailerAdapter extends RecyclerView.Adapter<EditTrailerAdapter.ViewHolder> {

    private static final int REQUEST_CODE_PICK_VIDEO = 123;
    private int selectedPosition = -1;
    private Uri selectedVideoUri;
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    private EditMovieActivity activity;

    @Override
    public void onBindViewHolder(@NonNull EditTrailerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            if(!EditMovieActivity.videos.get(position).equals(EditMovieActivity.defaultAddTrailer))
            {
                holder.movietrailer.setBackground(null);
                holder.texttrailer.setText("");
                holder.imtrailer.setImageResource(0);
                if(EditMovieActivity.isVideoLoaded.get(position)==false)
                    holder.bindVideo(EditMovieActivity.videos.get(position));
                holder.playButton.setImageResource(R.drawable.play_icon);
                holder.EditTrailer.setVisibility(View.VISIBLE);
                holder.deleteTrailer.setVisibility(View.INVISIBLE);
                holder.videoSeekBar.setProgress(0);
                EditMovieActivity.isVideoLoaded.set(position,true);
                holder.endTime.setText(""+VideoAdapter.convertIntoTime(holder.movietrailer.getDuration()-0));
                holder.movietrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);
                        holder.playButton.setVisibility(View.VISIBLE);
                        holder.videoSeekBar.setVisibility(View.VISIBLE);
                        holder.endTime.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.movietrailer.isPlaying())
                                {
                                    holder.playButton.setVisibility(View.INVISIBLE);
                                    holder.videoSeekBar.setVisibility(View.INVISIBLE);
                                    holder.endTime.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 1*5000);

                    }

                });
                holder.videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            holder.movietrailer.seekTo(progress);
                            int currentPosition = holder.movietrailer.getCurrentPosition();
                            holder.endTime.setText("" + VideoAdapter.convertIntoTime(holder.movietrailer.getDuration() - currentPosition));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                holder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);

                        if(holder.movietrailer.isPlaying())
                        {
                            holder.movietrailer.pause();
                            holder.playButton.setImageResource(R.drawable.play_icon);
                        }
                        else
                        {
                            holder.playButton.setImageResource(R.drawable.pause_icon);
                            holder.movietrailer.start();
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    holder.playButton.setVisibility(View.INVISIBLE);
                                    holder.videoSeekBar.setVisibility(View.INVISIBLE);
                                    holder.endTime.setVisibility(View.INVISIBLE);
                                }
                            }, 1*5000);
                        }
                    }
                });
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (holder.movietrailer.getDuration()>0){
                            int currentPosition = holder.movietrailer.getCurrentPosition();
                            holder.videoSeekBar.setProgress(currentPosition);
                            holder.endTime.setText(""+VideoAdapter.convertIntoTime(holder.movietrailer.getDuration()-currentPosition));
                        }
                        handler.postDelayed(this,0);
                    }
                };
                handler.postDelayed(runnable,500);
            }

            else if(EditMovieActivity.videoUris.get(position)!=EditMovieActivity.defaultUri)
            {
                holder.movietrailer.setBackground(null);
                holder.texttrailer.setText("");
                holder.imtrailer.setImageResource(0);
                holder.movietrailer.setVideoURI(EditMovieActivity.videoUris.get(position));
                holder.playButton.setImageResource(R.drawable.play_icon);
                holder.EditTrailer.setVisibility(View.VISIBLE);
                holder.deleteTrailer.setVisibility(View.INVISIBLE);
                holder.videoSeekBar.setProgress(0);
                holder.endTime.setText(""+VideoAdapter.convertIntoTime(holder.movietrailer.getDuration()-0));
                holder.movietrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);
                        holder.playButton.setVisibility(View.VISIBLE);
                        holder.videoSeekBar.setVisibility(View.VISIBLE);
                        holder.endTime.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.movietrailer.isPlaying())
                                {
                                    holder.playButton.setVisibility(View.INVISIBLE);
                                    holder.videoSeekBar.setVisibility(View.INVISIBLE);
                                    holder.endTime.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 1*5000); // wait for 5 seconds

                    }

                });
                holder.videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            holder.movietrailer.seekTo(progress);
                            int currentPosition = holder.movietrailer.getCurrentPosition();
                            holder.endTime.setText("" + VideoAdapter.convertIntoTime(holder.movietrailer.getDuration() - currentPosition));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                holder.movietrailer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.videoSeekBar.setMax(holder.movietrailer.getDuration());
                    }
                });
                holder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);
                        if(holder.movietrailer.isPlaying())
                        {
                            holder.movietrailer.pause();
                            holder.playButton.setImageResource(R.drawable.play_icon);
                        }
                        else
                        {
                            holder.playButton.setImageResource(R.drawable.pause_icon);
                            holder.movietrailer.start();
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    holder.playButton.setVisibility(View.INVISIBLE);
                                    holder.videoSeekBar.setVisibility(View.INVISIBLE);
                                    holder.endTime.setVisibility(View.INVISIBLE);
                                }
                            }, 1*5000);
                        }
                    }
                });
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (holder.movietrailer.getDuration()>0){
                            int currentPosition = holder.movietrailer.getCurrentPosition();
                            holder.videoSeekBar.setProgress(currentPosition);
                            holder.endTime.setText(""+VideoAdapter.convertIntoTime(holder.movietrailer.getDuration()-currentPosition));
                        }
                        handler.postDelayed(this,0);
                    }
                };
                handler.postDelayed(runnable,500);
            }
            else {
                holder.movietrailer.setBackgroundResource(R.drawable.background_add_movie1);
                holder.texttrailer.setText("Upload Video");
                holder.imtrailer.setImageResource(R.drawable.symbol_image);
                holder.HideTimeLine();
                holder.EditTrailer.setVisibility(View.INVISIBLE);
                holder.movietrailer.setVideoURI(null);
                holder.deleteTrailer.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.deleteTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);
                        EditMovieActivity.videos.remove(position);
                        EditMovieActivity.videoUris.remove(position);
                        Toast toast = Toast.makeText(holder.itemView.getContext(),"Delete trailer layout success!!!", Toast.LENGTH_SHORT);
                        toast.show();
                        notifyDataSetChanged();
                    }
                });

                holder.movietrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissKeyboard(view);
                        activity.pickVideo.launch(new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                                .build());
                        setSelectedPosition(position);
                    }
                });

            }

        }
        catch (Exception e)
        {}
        holder.EditTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboard(view);
                PopupMenu popup = new PopupMenu(view.getContext(), holder.EditTrailer);
                popup.inflate(R.menu.promo_menu);
                SpannableString s = new SpannableString("Change");
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                popup.getMenu().getItem(0).setTitle(s);
                SpannableString delete = new SpannableString("Delete");
                delete.setSpan(new ForegroundColorSpan(Color.RED), 0, delete.length(), 0);
                popup.getMenu().getItem(1).setTitle(delete);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.promo_edit:
                            {
                                activity.pickVideo.launch(new PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                                        .build());
                                setSelectedPosition(position);
                            }
                            return true;
                            case R.id.promo_delete: {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext(), R.style.CustomAlertDialog);
                                LayoutInflater factory = LayoutInflater.from(holder.itemView.getContext());
                                final View deleteDialogView = factory.inflate(R.layout.yes_no_dialog, null);
                                TextView textDialog = deleteDialogView.findViewById(R.id.message);
                                textDialog.setText("Do you sure to delete the trailer video?");
                                alertDialog.setView(deleteDialogView);
                                AlertDialog OptionDialog = alertDialog.create();
                                OptionDialog.show();
                                TextView Cancel = deleteDialogView.findViewById(R.id.Cancel_Button);
                                TextView Delete = deleteDialogView.findViewById(R.id.DeleteButton);
                                Delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dismissKeyboard(view);
                                        EditMovieActivity.videos.remove(position);
                                        EditMovieActivity.videoUris.remove(position);
                                        Toast toast = Toast.makeText(holder.itemView.getContext(),"Delete trailer layout success!!!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        notifyDataSetChanged();
                                        OptionDialog.dismiss();
                                    }
                                });
                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        OptionDialog.dismiss();
                                    }
                                });
                            }
                            return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });
    }

    public EditTrailerAdapter(EditMovieActivity activity)
    {
        this.activity=activity;
    }
    @Override
    public int getItemCount() {
        return EditMovieActivity.videos.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        VideoView movietrailer;
        ImageView imtrailer;
        TextView texttrailer;
        ImageView EditTrailer;
        ImageView deleteTrailer;
        ImageView playButton;
        SeekBar videoSeekBar;
        TextView endTime;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            movietrailer = (VideoView) itemView.findViewById(R.id.movietrailer);
            imtrailer = (ImageView) itemView.findViewById(R.id.imtrailer);
            texttrailer = (TextView) itemView.findViewById(R.id.texttrailer);
            EditTrailer= itemView.findViewById(R.id.EditTrailer);
            playButton=itemView.findViewById(R.id.PlayButton);
            videoSeekBar = itemView.findViewById(R.id.videoView_seekbar);
            endTime = itemView.findViewById(R.id.videoView_endtime);
            progressBar = itemView.findViewById(R.id.progressBar);
            deleteTrailer=itemView.findViewById(R.id.DeleteTrailer);
        }
        public void bindVideo(String videoUrl) {
            showLoading();

            movietrailer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    hideLoading();
                    showTimeLine();
                    videoSeekBar.setMax(movietrailer.getDuration());
                }
            });

            movietrailer.setVideoPath(videoUrl);
            movietrailer.requestFocus();
        }

        private void showLoading() {
            progressBar.setVisibility(View.VISIBLE);
            HideTimeLine();
        }
        private void showTimeLine()
        {
            playButton.setVisibility(View.VISIBLE);
            videoSeekBar.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
        }
        private void HideTimeLine()
        {
            playButton.setVisibility(View.GONE);
            videoSeekBar.setVisibility(View.GONE);
            endTime.setVisibility(View.GONE);
        }

        private void hideLoading() {
            progressBar.setVisibility(View.GONE);
            showTimeLine();
        }
    }
    public int getSelectedPosition()
    {
        return  selectedPosition;
    }
    @NonNull
    @Override
    public EditTrailerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_trailer_movie, parent, false);
        return new EditTrailerAdapter.ViewHolder(itemView);
    }

    public void updateVideoElement(Uri videoUri, int position)
    {
        if (position >= 0 && position < EditMovieActivity.videos.size()) {
            selectedVideoUri = videoUri;
            EditMovieActivity.videoUris.set(position, videoUri);
            notifyItemChanged(position);
        }
    }
    void dismissKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}