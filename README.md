# usbdetect-sample
Usb Detection Usb Debugging
Note :  When in Charging Usb Detection will not detect usb in connected but if when connected to pc will recognize 


Getting Started 
- Clone repository 
- build with project with minimum android studio artic fox 


How to Getting Result is Usb Connected or Disconnected 
As LifeCycleObservar 
 ```class MainActivity : AppCompatActivity(),UsbConnectionCallback {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //register the UsbDetectionObservable to activity or fragment 
        lifecycle.addObserver(UsbDetectionObservable(context = this,this))
    }

    override fun onConnected() {
        binding.tvStatus.text = "Usb : onConnected"
    }

    override fun onDisconnected() {
        binding.tvStatus.text = "Usb : onDisconnected"
    }

}```

As Coroutine Flow
``` class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch{
            usbPlugReceiverAsFlow(this).collectLatest {
              if(it){
                binding.tvStatus.text = "Usb : onConnected"
              }else{
                binding.tvStatus.text = "Usb : onDisconnected"
              }
            }
        }
    }
}```


