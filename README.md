
Caching is a way of temporarily storing data fetched from a network on a device's storage, so that we can access it at a later time when the device is offline or if we want to access the same data again.
In the following section, I’ll do a Retrofit Request with OkHttp as the Client and using RxJava.
We’ll cache the requests such that they can be displayed the next time if there is no internet/problem in getting the latest request.

I use several libraries in this project :

### Library
- RxJava 
- Retrofit
- OkHttp
- Glide
- Lombok
- ButterKnife

### Benefits of Caching
- Reduces bandwidth consumption
- Saves you time you'd spend waiting for the server to give you the network response.
- Saves the server the burden of additional traffic.
- if you need to access the same network resource again after having accessed it recently, your device won't need to make a request to the server, it'll get the cached response instead.

# Creating a cache
### Step 1 : Define a Class to Check for internet connectivity
We first need to have a class in our app that checks for internet connectivity.

```ruby
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if(instance == null){
            instance = this;
        }
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public static boolean hasNetwork(){
        return instance.isNetworkConnected();
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
```

### Step 2 : Defining the size of the cahce
The following line of code specifies a cache of 5MB.

```ruby
private static final long = 5 * 1024 * 1024;

```

### Step 3 : Creating Method Cache and Interceptor

```ruby
  private static Cache cache(){
        return new Cache(new File(MyApplication.getInstance().getCacheDir(),"someIdentifier"), cacheSize);
    }
```

And we need to add an Interceptor, which is responsible for observing and modifying request going out and the corresponding responses coming back in.

This interceptor will be called both if the network is available and if the network is not available.
Setting the max age to 7 Days, which means the cache will be valid for 7 Days. 
For example, the first request will be getting response from the server, and the following request made within 7 Days of the first request will be getting response from the cache.
```ruby
 private static Interceptor offlineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "offline interceptor: called.");
                Request mRequest = chain.request();

                // prevent caching when network is on. For that we use the "networkInterceptor"
                if (!MyApplication.hasNetwork()) {
                    CacheControl mCacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    mRequest = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(mCacheControl)
                            .build();
                }

                return chain.proceed(mRequest);
            }
        };
    }
```

This interceptor will be called ONLY if the network is available

```ruby
    private static Interceptor networkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "network interceptor: called.");

                Response mResponse = chain.proceed(chain.request());

                CacheControl mCacheControl = new CacheControl.Builder()
                        .maxAge(5, TimeUnit.SECONDS)
                        .build();

                return mResponse.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, mCacheControl.toString())
                        .build();
            }
        };
    }
```
### Step 4 : Creating the OkHttpClient with an interceptor

The following gist explains how to add this to our OkHttpClient, along with adding our cache :

```ruby
  OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(makeLoggingInterceptor(true))
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offlineInterceptor())
                .build();
```

Now we need to add this OkHttpClient to our Retrofit instance. Here's how to do that :

```ruby
       api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Api.class);
```

# Cappy Hodding


