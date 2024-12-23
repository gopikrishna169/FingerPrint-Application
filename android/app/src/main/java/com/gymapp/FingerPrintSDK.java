package com.gymapp;

import com.nitgen.SDK.AndroidBSP.NBioBSPJNI;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.CAPTURED_DATA;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.CAPTURE_CALLBACK;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.CAPTURE_QUALITY_INFO;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.INIT_INFO_0;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.IndexSearch.FP_INFO;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.IndexSearch.SAMPLE_INFO;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.NFIQInfo;
import com.nitgen.SDK.AndroidBSP.StaticVals;
import android.app.Activity;
import java.lang.Thread;

public class FingerPrintSDK {
    private NBioBSPJNI bsp;
    public static final int QUALITY_LIMIT = 60;

    private NBioBSPJNI.Export exportEngine;
    private NBioBSPJNI.IndexSearch indexSearch;
    private byte[] byTemplate1;
    private byte[] byTemplate2;

    private byte[] byCapturedRaw1;
    private int nCapturedRawWidth1;
    private int nCapturedRawHeight1;

    private byte[] byCapturedRaw2;
    private int nCapturedRawWidth2;
    private int nCapturedRawHeight2;

    Activity activity;

    public void FingerPrintSDK() {
    }

    public void init() {
        try{
            NBioBSPJNI.CURRENT_PRODUCT_ID = 0;
            if (bsp == null) {
                bsp = new NBioBSPJNI("010701-613E5C7F4CC7C4B0-72E340B47E034015", this.activity, mCallback);
                String msg = null;
                if (bsp.IsErrorOccured())
                    msg = "NBioBSP Error: " + bsp.GetErrorCode();
                else {
                    msg = "SDK Version: " + bsp.GetVersion();
                    exportEngine = bsp.new Export();
                    indexSearch = bsp.new IndexSearch();
                }
            }
            Thread.sleep(2000);
            bsp.OpenDevice();
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getFingerPrintData() {
        return new String(byCapturedRaw1);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    CAPTURE_CALLBACK mCallback = new CAPTURE_CALLBACK() {

        public void OnDisConnected() {
            NBioBSPJNI.CURRENT_PRODUCT_ID = 0;
        }

        public void OnConnected() {
            new Thread(new Runnable() {
                public void run() {
                    OnCapture1(24 * 60 * 60 * 1000);
                }
            }).start();
        }

        public int OnCaptured(CAPTURED_DATA capturedData) {
//            if (capturedData.getImage() != null) {
//                if (bCapturedFirst) {
//                    img_fp_src.setImageBitmap(capturedData.getImage());
//                    img_captured_finger.setImageBitmap(capturedData.getImage());
//                } else {
//                    img_fp_dst.setImageBitmap(capturedData.getImage());
//                }
//            }

            // quality : 40~100
            if (capturedData.getImageQuality() >= QUALITY_LIMIT) {
                return NBioBSPJNI.ERROR.NBioAPIERROR_USER_CANCEL;
            } else if (capturedData.getDeviceError() != NBioBSPJNI.ERROR.NBioAPIERROR_NONE) {
                return capturedData.getDeviceError();
            } else {
                return NBioBSPJNI.ERROR.NBioAPIERROR_NONE;
            }
        }

    };

    int nFIQ = 0;
    public synchronized void OnCapture1(int timeout) {
        try {
            NBioBSPJNI.FIR_HANDLE hCapturedFIR, hAuditFIR;
            NBioBSPJNI.CAPTURED_DATA capturedData;

            hCapturedFIR = bsp.new FIR_HANDLE();
            hAuditFIR = bsp.new FIR_HANDLE();
            capturedData = bsp.new CAPTURED_DATA();

//		bsp.Capture(NBioBSPJNI.FIR_PURPOSE.ENROLL,hCapturedFIR,timeout, hAuditFIR, capturedData,  mCallback,0, null);
            bsp.Capture(NBioBSPJNI.FIR_PURPOSE.ENROLL, hCapturedFIR, timeout, hAuditFIR, capturedData);

            if (bsp.IsErrorOccured()) {

            } else {
                NBioBSPJNI.INPUT_FIR inputFIR;
                inputFIR = bsp.new INPUT_FIR();
                // Make ISO 19794-2 data
                {
                    NBioBSPJNI.Export.DATA exportData;
                    inputFIR.SetFIRHandle(hCapturedFIR);
                    exportData = exportEngine.new DATA();
                    exportEngine.ExportFIR(inputFIR, exportData, NBioBSPJNI.EXPORT_MINCONV_TYPE.OLD_FDA);
                    if (bsp.IsErrorOccured()) {
                        return;
                    }
                    if (byTemplate1 != null)
                        byTemplate1 = null;
                    byTemplate1 = new byte[exportData.FingerData[0].Template[0].Data.length];
                    byTemplate1 = exportData.FingerData[0].Template[0].Data;
                }

                // Make Raw Image data
                {
                    NBioBSPJNI.Export.AUDIT exportAudit;
                    inputFIR.SetFIRHandle(hAuditFIR);
                    exportAudit = exportEngine.new AUDIT();
                    exportEngine.ExportAudit(inputFIR, exportAudit);
                    if (bsp.IsErrorOccured()) {
                        return;
                    }

                    if (byCapturedRaw1 != null)
                        byCapturedRaw1 = null;

                    byCapturedRaw1 = new byte[exportAudit.FingerData[0].Template[0].Data.length];
                    byCapturedRaw1 = exportAudit.FingerData[0].Template[0].Data;

                    nCapturedRawWidth1 = exportAudit.ImageWidth;
                    nCapturedRawHeight1 = exportAudit.ImageHeight;

                    NFIQInfo info = bsp.new NFIQInfo();
                    info.pRawImage = byCapturedRaw1;
                    info.nImgWidth = nCapturedRawWidth1;
                    info.nImgHeight = nCapturedRawHeight1;

                    bsp.getNFIQInfoFromRaw(info);

                    if (bsp.IsErrorOccured()) {
                        return;
                    }

                    nFIQ = info.pNFIQ;

                    // ISO 19794-4
                    {
                        NBioBSPJNI.ISOBUFFER ISOBuffer = bsp.new ISOBUFFER();
                        bsp.ExportRawToISOV1(exportAudit, ISOBuffer, false, NBioBSPJNI.COMPRESS_MODE.NONE);
//					bsp.ExportRawToISOV1(exportAudit, ISOBuffer, false, NBioBSPJNI.COMPRESS_MODE.WSQ);

                        if (bsp.IsErrorOccured()) {
                            return;
                        }

                        NBioBSPJNI.NIMPORTRAWSET rawSet = bsp.new NIMPORTRAWSET();
                        bsp.ImportISOToRaw(ISOBuffer, rawSet);

                        if (bsp.IsErrorOccured()) {
                            return;
                        }

                        if (byCapturedRaw1 != null)
                            byCapturedRaw1 = null;

                        byCapturedRaw1 = new byte[rawSet.RawData[0].Data.length];
                        byCapturedRaw1 = rawSet.RawData[0].Data;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}