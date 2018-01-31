package com.icodeman.netproject;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icodeman.baselib.activity.BaseActivity;

/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2018/1/30
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvIpAddress, tvSubnetAddress, tvSubnetNumber;
    private Button btGetAddress, btCheckAddress;
    private TextView infoView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvIpAddress = getView(R.id.ip_address);
        tvIpAddress.setText("210.114.105.164");
        tvSubnetAddress = getView(R.id.subnet_address);
        tvSubnetAddress.setText("255.255.255.224");
        tvSubnetNumber = getView(R.id.subnet_number);
        tvSubnetNumber.setText("24");

        btGetAddress = getView(R.id.by_subnet_address);
        btGetAddress.setOnClickListener(this);
        btCheckAddress = getView(R.id.by_subnet_number);
        btCheckAddress.setOnClickListener(this);

        infoView = getView(R.id.info);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.by_subnet_address) {
            getInfoBySubnetAddress(tvIpAddress.getText().toString(), tvSubnetAddress.getText().toString());
        } else if (v.getId() == R.id.by_subnet_number) {
            getInfoBySubnetNumber(tvIpAddress.getText().toString(), tvSubnetNumber.getText().toString());
        }
    }

    private void getInfoBySubnetNumber(String ip, String subnet) {
        int cidr = Integer.parseInt(subnet);
        if (cidr > 31) {
            addLineInfo("输入错误");
            return;
        }
        int[] ipInfo = getArrayFromString(ip);
        int[] subnetInfo = getSubnetInfoFromCIDR(cidr);
        if(!checkAddressInfo(ipInfo) ||!checkAddressInfo(subnetInfo)){
            addLineInfo("出现错误");
            return;
        }
        refreshInfo(ipInfo, subnetInfo);
    }

    private void getInfoBySubnetAddress(String ip, String subnet) {
        int[] ipInfo = getArrayFromString(ip);
        int[] subnetInfo = getArrayFromString(subnet);
        if(!checkAddressInfo(ipInfo) ||!checkAddressInfo(subnetInfo)){
            addLineInfo("出现错误");
            return;
        }
        refreshInfo(ipInfo, subnetInfo);
    }

    private boolean checkAddressInfo(int[] info){
        for (int i:info){
            if(i<0||i>255){
                return false;
            }
        }
        return true;
    }

    private void refreshInfo(int[] ipInfo, int[] subnetInfo) {
        infoView.setText("");
        addLineInfo("可用Ip个数：");
        addLineInfo(getPossibleCount(subnetInfo) + "个");
        addLineInfo("-----------");
        addLineInfo("子网掩码：");
        addLineInfo(getStringFromArray(subnetInfo));
        addLineInfo("-----------");
        addLineInfo("网络地址：");
        addLineInfo(getStringFromArray(getNetAddress(ipInfo, subnetInfo)));
        addLineInfo("-----------");
        addLineInfo("广播地址：");
        addLineInfo(getStringFromArray(getBroadcastAddress(ipInfo, subnetInfo)));
    }

    private void addLineInfo(String info) {
        StringBuilder sb = new StringBuilder(infoView.getText());
        sb.append('\n');
        sb.append(info);
        infoView.setText(sb.toString());
    }

    private int[] getSubnetInfoFromCIDR(int subnetNumber) {
        int[] back = new int[4];
        int temp = 0;
        int position = 0;
        for (int i = 0; i < subnetNumber; i++) {
            back[position] += 1 << (7 - temp);
            temp++;
            if (temp % 8 == 0) {
                temp = 0;
                position++;
            }
        }
        return back;
    }

    /**
     * 获得可用的IP个数
     *
     * @param subnet
     * @return
     */
    private int getPossibleCount(int[] subnet) {
        if (subnet.length != 4) {
            return -1;
        }
        int sum = 0;
        int weight = 1;
        for (int i = subnet.length - 1; i >= 0; i--) {
            sum += (255 - subnet[i]) * weight;
            weight *= 256;
        }
        return sum - 1;
    }

    /**
     * 获取网络地址
     *
     * @param ip
     * @param subnet
     * @return
     */
    private int[] getNetAddress(int[] ip, int[] subnet) {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = ip[i] & subnet[i];//与运算
        }
        return result;
    }

    /**
     * 获取广播地址
     *
     * @param ip
     * @param subnet
     * @return
     */
    private int[] getBroadcastAddress(int[] ip, int[] subnet) {
        int[] temp = new int[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = 255 - subnet[i];//非运算
        }
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = ip[i] | temp[i];//或运算
        }
        return result;
    }

    /**
     * 将数组类型的地址转为字符串类型
     *
     * @param info
     * @return
     */
    private String getStringFromArray(int[] info) {
        StringBuilder sb = new StringBuilder("");
        for (int item : info) {
            sb.append(item);
            sb.append(".");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * 将字符串类型的地址转为数组
     *
     * @param address
     * @return
     */
    private int[] getArrayFromString(String address) {
        String[] temp = address.split("\\.");
        if (temp.length != 4) {
            return new int[]{0, 0, 0, 0};
        }
        int[] back = new int[4];
        for (int i = 0; i < back.length; i++) {
            back[i] = Integer.parseInt(temp[i]);
        }
        return back;
    }
}
