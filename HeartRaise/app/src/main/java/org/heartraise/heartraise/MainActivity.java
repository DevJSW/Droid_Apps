package org.heartraise.heartraise;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog mProgress = null;
    private RecyclerView mHeartRaiseList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth auth;
    private ImageButton mImageButton;

    ListView listView = null;

    private Uri mImageUri = null;
    private static int GALLERY_REQUEST =1;

    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("HeartRaise");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseUsers.keepSynced(true);

        mImageButton = (ImageButton) findViewById(R.id.editImageBtn);



        mHeartRaiseList = (RecyclerView) findViewById(R.id.heartraise_list);
        mHeartRaiseList.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        mHeartRaiseList.setLayoutManager(new LinearLayoutManager(this));
        mProgress = new ProgressDialog(this);

        /**************************open Donate dialog*************************/

        listView = new ListView(this);
        String[] items= {"M-Pesa", "Airtel Money", "Equitel"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.share_items, R.id.txtitem,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ViewGroup vg = (ViewGroup)view;
                TextView txt = (TextView)vg.findViewById(R.id.txtitem);
                Toast.makeText(MainActivity.this, txt.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });


        checkUserExists();

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
            @Override
            protected void populateViewHolder(heartraiseViewHolder viewHolder, heartraise model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setStory(model.getStory());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.setLikeBtn(post_key);
                /************open commentsActivity****************/
                viewHolder.mComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, CommentsActivity.class));
                    }
                });
                /************open donateDialog****************/
                viewHolder.mDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(listView.getParent()!=null)
                            ((ViewGroup)listView.getParent()).removeView(listView);

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            //builder.setPositiveButton("OK",null);
                            builder.setCancelable(true);
                            builder.setView(listView);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                    }
                });



              viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;



                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {

                                            mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;

                                        } else {

                                            mDatabaseLike.child(post_key).child(auth.getCurrentUser().getUid()).setValue("username");

                                            mProcessLike = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                    }
                });

            }

        };

        mHeartRaiseList.setAdapter(firebaseRecyclerAdapter);


    }



    public static class heartraiseViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton mLikebtn, mComment, mDonate, mShare;
        DatabaseReference mDatabaseLike;
        FirebaseAuth auth;

        public heartraiseViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mLikebtn = (ImageButton) mView.findViewById(R.id.addLike_Button);
            mComment = (ImageButton) mView.findViewById(R.id.commentBtn);
            mDonate = (ImageButton) mView.findViewById(R.id.donateBtn);
            mShare = (ImageButton) mView.findViewById(R.id.shareBtn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);

        }

       public void setLikeBtn(final String post_key) {


            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(auth.getCurrentUser().getUid())) {

                        mLikebtn.setImageResource(R.drawable.purple_like_btn);

                    } else {

                        mLikebtn.setImageResource(R.drawable.like);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setStory(String story) {

            TextView post_story = (TextView) mView.findViewById(R.id.post_story);
            post_story.setText(story);
        }

        public void setUsername(String username) {

            TextView post_story = (TextView) mView.findViewById(R.id.post_username);
            post_story.setText(username);
        }

        public void setImage(final Context ctx, final String image) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {


                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }


    }



    private void checkUserExists() {

        this.mProgress = ProgressDialog.show(this, "HeartRaise",
                "Loading...");

        final String User_id = auth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(User_id)) {

                    mProgress.dismiss();

                    Toast.makeText(getApplicationContext(), "Add your profile photo & Username!", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(MainActivity.this, Editprofile.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(intent);
                    //finish();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }







    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action

        } else if (id == R.id.nav_aboutApp) {

        } else if (id == R.id.nav_settings) {

            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_invite) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.editImageBtn) {

            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
