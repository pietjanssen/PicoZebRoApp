package hogeschoolrotterdam.picozebroapp;

/**
 * Created by Boris_000 on 21-2-2018.
 */

public class CameraActivity extends BaseActivity{
    @Override
    int getContentViewId() {
        return R.layout.activity_camera;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_camera;
    }
}
