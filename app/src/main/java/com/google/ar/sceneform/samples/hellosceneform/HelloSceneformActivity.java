/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
  private static final String TAG = HelloSceneformActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;
  private ArFragment arFragment;
  private ModelRenderable andyRenderable;
  private Renderable myRender;
  private int msg = 1;
  private Button button;

  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      if (!checkIsSupportedDeviceOrFinish(this)) {
          return;
      }

      setContentView(R.layout.activity_ux);
      arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
      /*Create and define our button actions*/
      button = (Button) findViewById(R.id.button4);
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              changeMsg();
          }
      });
      if(!checkIsSupportedDeviceOrFinish((this))){
          return;
      }
      // When you build a Renderable, Sceneform loads its resources in the background while returning
      // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
//      ModelRenderable.builder()
//              .setSource(this, R.raw.andy)
//              .build()
//              .thenAccept(renderable -> andyRenderable = renderable)
//              .exceptionally(
//                      throwable -> {
//                          Toast toast =
//                                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
//                          toast.setGravity(Gravity.CENTER, 0, 0);
//                          toast.show();
//                          return null;
//                      });

      arFragment.setOnTapArPlaneListener(
              (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                  // Create the Anchor.
                  Pose pose;
                  float [] xyz = {(float) 0,.3f,-6};
                  float [] rot = {(float) 0,0,0,1};
                  pose = new Pose(xyz,rot);

                  Anchor anchor = plane.createAnchor(pose);
                  AnchorNode anchorNode = new AnchorNode(anchor);
                  anchorNode.setParent(arFragment.getArSceneView().getScene());

                  // Create the transformable andy and add it to the anchor.
                  TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                  andy.setParent(anchorNode);
                  andy.setRenderable(myRender);
                  andy.select();

              });


  }

    private void changeMsg() {

        if(msg == 1){
            button.setText("1");
            msg=2;
            ViewRenderable.builder()
                    .setView(this,R.layout.letter_image)
                    .build()
                    .thenAccept(renderable -> myRender = renderable
                    );

        }else if(msg == 2){
            button.setText("2");
            msg = 3;
            ViewRenderable.builder()
                    .setView(this,R.layout.letter_image_rotate)
                    .build()
                    .thenAccept(renderable -> myRender = renderable
                    );
        }else if(msg == 3){
            button.setText("3");
            msg = 4;
            ViewRenderable.builder()
                    .setView(this,R.layout.letter_image_rotate2)
                    .build()
                    .thenAccept(renderable -> myRender = renderable
                    );
        }else{
            button.setText("4");
            msg = 1;
            ViewRenderable.builder()
                    .setView(this,R.layout.letter_web_rotate3)
                    .build()
                    .thenAccept(renderable -> myRender = renderable
                    );
        }
        return;
    }


    /**
   * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
   * on this device.
   *
   * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
   *
   * <p>Finishes the activity if Sceneform can not run
   */
  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }
}
