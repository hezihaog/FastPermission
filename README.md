# FastPermission
一个快速适配Android6.0权限的库。

### 快速使用
- gradle 依赖
- compile 'com.hzh:fast-permission:1.0.8'

### 文章地址
- [简书链接](https://www.jianshu.com/p/f61f5ce533e5)

### 一、引言
    日常Android开发中，要兼容6.0以上版本时，躲不过难免就是6.0新增的运行时权限申请了，那作为Android开发者，就必须掌握并适配这种情况。

#### 二、该库特点
1. 调用方便，调用处只需要提供3个参数
	- FragmentActivity实例
	- CallBack回调实例
	- 要申请的权限数组

#### 三、库的思想
1. 将请求权限的任务，转移到一个代理Fragment，权限onResult方法转移到该类。
2. 其他项目需要接入6.0权限时，不需要更改Activity内代码，只需添加一句request方法调用，提供回调和权限数组列表。
3. RxPermission也是一样的思想，不过作者不熟悉RxJava，故自己写一个。

#### 四、结构

## 1、Callback
```java
public interface PermissionCallback {
    /**
     * 权限允许
     */
    void onGranted();

    /**
     * 权限拒绝
     * @param perms 被拒绝的权限集合
     */
    void onDenied(List<String> perms);
}
```

## 2、FastPermission
```java
public class FastPermission {
    private FastPermission() {
    }

    private static class Singleton {
        private static final FastPermission instance = new FastPermission();
    }

    public static FastPermission getInstance() {
        return Singleton.instance;
    }

    /**
     * 小于6.0则不检查权限
     *
     * @return 返回true代表版本号大于6.0需要检查权限
     */
    private boolean isNeedCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查指定权限是否已经获取
     */
    public boolean isAccept(Context context, String permission) {
        if (!isNeedCheck()) {
            return true;
        } else {
            return isNeedCheck() && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 使用Activity申请权限
     *
     * @param activity 要注入权限申请代理的FragmentActivity
     * @param callback 权限申请 成功、失败回调
     * @param perms    权限列表数组
     */
    public void request(@NonNull FragmentActivity activity, @NonNull PermissionCallback callback, @NonNull String[] perms) {
        PermissionDelegateFragment delegate = findDelegate(activity);
        if (delegate != null) {
            delegate.requestPermission(activity, callback, perms);
        }
    }

    /**
     * 使用Fragment申请权限
     *
     * @param fragment 使用的Fragment
     * @param callback 权限申请 成功、失败回调
     * @param perms    权限列表数组
     */
    public void request(@NonNull Fragment fragment, @NonNull PermissionCallback callback, @NonNull String[] perms) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null && !activity.isFinishing()) {
            PermissionDelegateFragment delegate = findDelegate(activity);
            if (delegate != null) {
                delegate.requestPermission(activity, callback, perms);
            }
        }
    }


    /**
     * 构建申请权限用的隐藏的fragment
     */
    private PermissionDelegateFragment findDelegate(FragmentActivity activity) {
        return PermissionDelegateFinder.getInstance().find(activity);
    }
}
```
- 权限适配只在6.0以上，6.0默认不检测权限。
- manager类提供request()方法，提供给Activity调用。
- findDelegate()方法，内部调用Finder进行查找、添加Fragment。

## 3、PermissionDelegateFinder
```java
public class PermissionDelegateFinder {
    private static final String DELEGATE_FRAGMENT_TAG = PermissionDelegateFragment.class.getSimpleName() + "Tag";

    private static class Singleton {
        private static final PermissionDelegateFinder instance = new PermissionDelegateFinder();
    }

    public static PermissionDelegateFinder getInstance() {
        return Singleton.instance;
    }

    /**
     * 添加隐藏权限fragment
     */
    public PermissionDelegateFragment find(@NonNull FragmentActivity activity) {
        PermissionDelegateFragment fragment = null;
        if (activity != null && !activity.isFinishing()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            fragment = (PermissionDelegateFragment) fm.findFragmentByTag(DELEGATE_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = PermissionDelegateFragment.newInstance();
                fm.beginTransaction()
                        .add(fragment, DELEGATE_FRAGMENT_TAG)
                        .commitAllowingStateLoss();
            }
        }
        return fragment;
    }
}
```
- find()，传入FragmentActivity，使用其中的FragmentManager进行查找、添加，如果找不到，则添加。

## PermissionDelegateFragment
```java
public class PermissionDelegateFragment extends LifecycleFragment {
    //权限回调的标识
    private static final int REQUEST_CODE = 0X0122;
    private SparseArrayCompat<RequestEntry> callbacks = new SparseArrayCompat<RequestEntry>();

    public static PermissionDelegateFragment newInstance() {
        return new PermissionDelegateFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        popAll();
        getLifecycle().removeAllListener();
    }

    /**
     * 请求操作必须在OnAttach后调用
     *
     * @param entry 请求包装对象
     */
    private void pushStack(final RequestEntry entry) {
        callbacks.put(entry.hashCode(), entry);
        this.getLifecycle().addListener(new SimpleFragmentLifecycleAdapter() {
            @Override
            public void onAttach() {
                super.onAttach();
                callbacks.get(entry.hashCode()).getRunnable().run();
                getLifecycle().removeListener(this);
            }
        });
    }

    /**
     * 结束任务，在集合中移除
     *
     * @param entry 要移除的请求包装对象
     */
    private void popStack(RequestEntry entry) {
        callbacks.remove(entry.hashCode());
    }

    /**
     * 移除所有callback
     */
    private void popAll() {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.clear();
        }
    }


    /**
     * 批量申请权限
     *
     * @param context  上下文
     * @param callback 权限允许、拒绝回调
     * @param perms    要申请的权限数组
     */
    public void requestPermission(final Context context, final PermissionCallback callback, final String[] perms) {
        if (callback != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onGranted();
            return;
        }
        pushStack(RequestEntry.newBuilder().withCallback(callback).withRunnable(new Runnable() {
            @Override
            public void run() {
                //只申请用户未允许的权限
                List<String> unGrantedList = new ArrayList<String>();
                for (String permission : perms) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        unGrantedList.add(permission);
                    }
                }
                if (unGrantedList.size() > 0) {
                    PermissionDelegateFragment.this.requestPermissions(unGrantedList.toArray(new String[]{}), REQUEST_CODE);
                } else {
                    if (callback != null) {
                        callback.onGranted();
                    }
                }
            }
        }).build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && callbacks != null && callbacks.size() > 0) {
                    for (int i = 0; i < callbacks.size(); i++) {
                        RequestEntry entry = callbacks.valueAt(i);
                        PermissionCallback callback = entry.getCallback();
                        //找出拒绝的权限
                        List<String> deniedList = new ArrayList<String>();
                        for (int j = 0; j < grantResults.length; j++) {
                            int grantResult = grantResults[j];
                            String permission = permissions[j];
                            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                                deniedList.add(permission);
                            }
                        }
                        //已全部允许
                        if (deniedList.isEmpty()) {
                            callback.onGranted();
                        } else {
                            callback.onDenied(deniedList);
                        }
                        popStack(entry);
                    }
                }
                break;
        }
    }
}
```

- requestPermission()，首先来看这个方法，这个是代理Fragment提供给finder调用的请求方法。
- onRequestPermissionsResult()则是请求回调，全部权限都允许了，则回调callback的onGranted()，有未允许的，则保存未回调的权限，并回调onDenied方法回传。
- pushStack方法，将任务添加到队列，popStack、popAll任务结束后，移除。pushStack方法里，还有个getLifecycle().addListener()调用，为什么呢？因为fragment刚进行new操作后，add进Activity后还需要Attach，才能进行申请，并且回调onRequestPermissionsResult()，否则会抛出异常，提示需要Attach，继续看。
- request很有可能在，onAttach之前调用，如果加入标志位，再保存callback和实例，再回调，能否更加优雅呢，就像将一个任务压进队列，当onAttach时将队列的任务都顺序执行。这里参考了glide的生命周期管理中，借用自身onAttach()回调，加入监听器回调。

## RequestEntry ，该类包裹每次请求的Callback和runnable。

```java
/**
 * Created by Hezihao on 2017/10/13.
 * 包裹请求的Callback和runnable
 */

public class RequestEntry {
    private  PermissionCallback callback;
    private Runnable runnable;

    private RequestEntry() {
    }

    public RequestEntry newInstance(Builder builder) {
        this.callback = builder.callback;
        this.runnable = builder.runnable;
        return this;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public PermissionCallback getCallback() {
        return callback;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public static class Builder {
        private  PermissionCallback callback;
        private Runnable runnable;

        public Builder withCallback(PermissionCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder withRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public RequestEntry build() {
            RequestEntry entry = new RequestEntry();
            return entry.newInstance(this);
        }
    }
}
```

## Lifecycle回调
```java
public interface Lifecycle {
    /**
     * 添加生命周期回调监听器
     * @param listener
     */
    void addListener(FragmentLifecycleListener listener);

    /**
     * 移除生命周期回调监听器
     * @param listener
     */
    void removeListener(FragmentLifecycleListener listener);

    /**
     * 移除所有生命周期回调监听器
     */
    void removeAllListener();
}
```

- 具体实现类

```java
public class FragmentLifecycle implements Lifecycle {
    //读写分离，避免遍历的同时add进集合，抛出高并发异常。
    private final CopyOnWriteArrayList<FragmentLifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<FragmentLifecycleListener>();
    private boolean isAttach;
    private boolean isStarted;
    private boolean isDestroyed;

    @Override
    public void addListener(FragmentLifecycleListener listener) {
        lifecycleListeners.add(listener);
        if (isAttach) {
            listener.onAttach();
        } else if (isDestroyed) {
            listener.onDestroy();
        } else if (isStarted) {
            listener.onStart();
        } else if (isStarted == false) {
            listener.onStop();
        } else {
            listener.onDetach();
        }
    }

    @Override
    public void removeListener(FragmentLifecycleListener listener) {
        if (lifecycleListeners.size() > 0 && lifecycleListeners.contains(listener)) {
            lifecycleListeners.remove(listener);
        }
    }

    @Override
    public void removeAllListener() {
        if (lifecycleListeners.size() > 0) {
            lifecycleListeners.clear();
        }
    }

    public void onAttach() {
        isAttach = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onAttach();
        }
    }

    public void onStart() {
        isStarted = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStart();
        }
    }

    public void onStop() {
        isStarted = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onStop();
        }
    }

    public void onDestroy() {
        isDestroyed = true;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDestroy();
        }
    }

    public void onDetach() {
        isAttach = false;
        for (FragmentLifecycleListener listener : lifecycleListeners) {
            listener.onDetach();
        }
    }
}
```

- LifecycleFragment使用该实现，PermissionDelegateFragment就是继承于该类

```java
public class LifecycleFragment extends Fragment {
    private FragmentLifecycle lifecycle;

    public LifecycleFragment() {
        this(new FragmentLifecycle());
    }

    @SuppressLint("ValidFragment")
    public LifecycleFragment(FragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public FragmentLifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lifecycle.onAttach();
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lifecycle.onDetach();
    }
}
```

## 使用
```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] permissionList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissionList = new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        } else {
            permissionList = new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        }

        Button request = (Button) findViewById(R.id.request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(permissionList);
            }
        });
    }

    /**
     * 请求权限
     *
     * @param perms 需要申请的权限数组
     */
    private void requestPermission(String[] perms) {
        FastPermission.getInstance().request(MainActivity.this, new PermissionCallback() {
            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "申请权限成功，可进行下一步操作", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(final List<String> perms) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("申请权限")
                        .setMessage("请允许app申请的所有权限，以便正常使用")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //再次请求未允许的权限
                                String[] againPerms = new String[perms.size()];
                                for (int j = 0; j < perms.size(); j++) {
                                    againPerms[j] = perms.get(j);
                                }
                                requestPermission(againPerms);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        }, perms);
    }
}
```

## 快速使用
- [Github链接](https://github.com/hezihaog/FastPermission)
- 求start，issue，push request ！
- gradle 依赖
```
compile 'com.hzh:fast-permission:1.0.8'
```