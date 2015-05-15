/*
 * Copyright (C) 2013 Keisuke SUZUKI
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * This code is checked by Galaxy S II and FT232RL
 */
package com.home.automation.ftdi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class FtdiManager {
    private final static String TAG = "FPGA_FIFO Activity";
    Activity ac;
    private static D2xxManager ftD2xx = null;
    private FT_Device ftDev;

    static final int READBUF_SIZE  = 256;
    byte[] rbuf  = new byte[READBUF_SIZE];
    char[] rchar = new char[READBUF_SIZE];
    int mReadSize=0;

    boolean mThreadIsStopped = true;
    Handler mHandler = new Handler();
    Thread mThread;

      public FtdiManager(FragmentActivity ac) {

    	this.ac=ac;
        try {
            ftD2xx = D2xxManager.getInstance(ac);
        } catch (D2xxManager.D2xxException ex) {
            Log.e(TAG,ex.toString());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        ac.registerReceiver(mUsbReceiver, filter);

    }


	/**
	 * @param writeString 
	 * 
	 */
	public void writeTodevice(String writeString) {
		if(ftDev!=null){
			Log.d("Home_automation", "ftDev  is not null");
		  ftDev.setLatencyTimer((byte)64);
		byte[] writeByte = writeString.getBytes();
		Log.d("Home_automation", "writing on device:ftDev ");
		ftDev.write(writeByte, writeString.length());
		}else{
			Log.d("Home_automation", "ftDev  is  null");
		}
	}

    public void onClickClose(View v) {
        closeDevice();
    }

    public void destroy() {
        mThreadIsStopped = true;
        ac.unregisterReceiver(mUsbReceiver);
    }


    public void openDevice() {
        int devCount = 0;
        devCount = ftD2xx.createDeviceInfoList(ac);

        Log.d(TAG, "Device number : "+ Integer.toString(devCount));

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);

        if(devCount <= 0) {
            return;
        }
        Log.d(TAG, "ftDev openByIndex called"+ftDev);
        ftDev = ftD2xx.openByIndex(ac, 0);
        Log.d(TAG, "ftDev is "+ftDev);
    	
        if(ftDev != null) {
            if(ftDev.isOpen()) {
                if(mThreadIsStopped) {
                    SetConfig(9600, (byte)8, (byte)1, (byte)0, (byte)0);
                    ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                  //  ftDev.restartInTask();
                 //   new Thread(mLoop).start();
                }
                return;
            }
        }
    }

//        private Runnable mLoop = new Runnable() {
//        @Override
//        public void run() {
//            int i;
//            int readSize;
//            mThreadIsStopped = false;
//            while(true) {
//                if(mThreadIsStopped) {
//                    break;
//                }
//
//                synchronized (ftDev) {
//                    readSize = ftDev.getQueueStatus();
//                    if(readSize>0) {
//                        mReadSize = readSize;
//                        if(mReadSize > READBUF_SIZE) {
//                            mReadSize = READBUF_SIZE;
//                        }
//                        ftDev.read(rbuf,mReadSize);
//
//                        // cannot use System.arraycopy
//                        for(i=0; i<mReadSize; i++) {
//                            rchar[i] = (char)rbuf[i];
//                        }
//
//                    } // end of if(readSize>0)
//                } // end of synchronized
//            }
//        }
//    };

    public void closeDevice() {
        mThreadIsStopped = true;
//        updateView(false);
        if(ftDev != null) {
            ftDev.close();
        }
    }


    public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
            Log.e(TAG, "SetConfig: device not open");
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
        case 7:
            dataBits = D2xxManager.FT_DATA_BITS_7;
            break;
        case 8:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        default:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        }

        switch (stopBits) {
        case 1:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        case 2:
            stopBits = D2xxManager.FT_STOP_BITS_2;
            break;
        default:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        }

        switch (parity) {
        case 0:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        case 1:
            parity = D2xxManager.FT_PARITY_ODD;
            break;
        case 2:
            parity = D2xxManager.FT_PARITY_EVEN;
            break;
        case 3:
            parity = D2xxManager.FT_PARITY_MARK;
            break;
        case 4:
            parity = D2xxManager.FT_PARITY_SPACE;
            break;
        default:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
        case 0:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        case 1:
            flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
            break;
        case 2:
            flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
            break;
        case 3:
            flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
            break;
        default:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        }

        // TODO : flow ctrl: XOFF/XOM
        // TODO : flow ctrl: XOFF/XOM
        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }


    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // never come here(when attached, go to onNewIntent)
                openDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
            }
        }
    };

}
