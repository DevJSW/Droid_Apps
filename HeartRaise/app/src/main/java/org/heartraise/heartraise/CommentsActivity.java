package org.heartraise.heartraise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentsActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private RecyclerView mCommentList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("HeartRaise");
        mProgress = new ProgressDialog(this);

        mCommentList = (RecyclerView) findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseComment = FirebaseDatabase.getInstance().getReference().child("Comments");
        mDatabaseComment.keepSynced(true);



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(

                Comment.class,
                R.layout.comment_row,
                CommentViewHolder.class,
                mDatabaseComment
        ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {

                viewHolder.setComment(model.getComment());

            }
        };

        mCommentList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setComment(String comment) {

            TextView post_comment = (TextView) mView.findViewById(R.id.post_comment);
            post_comment.setText(comment);

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        final TextView userinputtext = (TextView) findViewById(R.id.userinputtext);

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:

                if(item.getItemId() == R.id.action_comment) {

                    View view = (LayoutInflater.from(CommentsActivity.this)).inflate(R.layout.user_input, null);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CommentsActivity.this);
                    alertBuilder.setView(view);
                    final EditText userInput = (EditText) view.findViewById(R.id.userinput);
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Comments");
                    mProgress = new ProgressDialog(this);

                    alertBuilder.setTitle("Type comment...");
                    alertBuilder.setCancelable(true)
                            .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mProgress.setMessage("Posting comment...");
                                    mProgress.show();

                                    //userinputtext.setText(userInput.getText());
                                    String comment_val = userInput.getText().toString().trim();

                   /******************posting comment to database*********************/

                                    if (!TextUtils.isEmpty(comment_val)) {

                                        DatabaseReference newPost = mDatabase.push();

                                        newPost.child("comment").setValue(comment_val);
                                        mProgress.dismiss();

                                    }else {

                                        Toast.makeText(getApplicationContext(), "Hey!, comment please...", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                    Dialog dialog = alertBuilder.create();
                    dialog.show();

                }


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        return true;
    }





}
