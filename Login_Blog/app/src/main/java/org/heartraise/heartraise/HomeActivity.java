package org.heartraise.heartraise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mHeartRaiseList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("HeartRaise");


        mHeartRaiseList = (RecyclerView) findViewById(R.id.heartraise_list);
        mHeartRaiseList.setHasFixedSize(true);
        mHeartRaiseList.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<heartraise, heartraiseViewHolder> firebaseRecyclerAdapter = new  FirebaseRecyclerAdapter<heartraise, heartraiseViewHolder>(

                heartraise.class,
                R.layout.heartraise_row,
                heartraiseViewHolder.class,
                mDatabase


        ) {
            //@Override
            protected void populateViewHolder(heartraiseViewHolder viewHolder, heartraise model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setStory(model.getStory());
                viewHolder.setImage(getApplicationContext(), model.getImage());


            }

        };

        mHeartRaiseList.setAdapter(firebaseRecyclerAdapter);

    }




    public static class heartraiseViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public heartraiseViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setStory(String story) {

            TextView post_story = (TextView) mView.findViewById(R.id.post_story);
            post_story.setText(story);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add) {

            startActivity(new Intent(HomeActivity.this, PostActivity.class));

        }else if(item.getItemId() == R.id.action_settings) {

            startActivity(new Intent(HomeActivity.this, MainActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }



}
