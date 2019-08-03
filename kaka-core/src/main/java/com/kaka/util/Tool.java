package com.kaka.util;

import java.net.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author zhoukai
 */
public class Tool {

    /**
     * 根据时间和本机mac地址生成唯一标识字符串
     *
     * @return 唯一字符串
     */
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }

    /**
     * 获取本机网卡MAC地址
     *
     * @return mac地址字符串表示
     */
    public static String getLocalMacAddress() {
        try {
            InetAddress ias = InetAddress.getLocalHost();
            return getMacAddress(ias);
        } catch (UnknownHostException e) {
        }
        return null;
    }

    /**
     * 获取address网络地址主机的mac地址
     *
     * @param address 网络地址
     * @return mac地址
     */
    public static String getMacAddress(InetAddress address) {
        String mac;
        StringBuilder sb = new StringBuilder();
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni == null) {
                return null;
            }
            byte[] macs = ni.getHardwareAddress();
            if (macs == null) {
                return null;
            }
            for (int i = 0; i < macs.length; i++) {
                mac = Integer.toHexString(macs[i] & 0xFF);
                if (mac.length() == 1) {
                    mac = '0' + mac;
                }
                sb.append(mac);
            }
        } catch (SocketException e) {
        }
        mac = sb.toString();
        mac = mac.substring(0, mac.length() - 1);
        return mac;
    }

    /**
     * 获取本机IP地址列表
     *
     * @return 本机IP地址列表
     */
    public static final List<InetAddress> getLocalInetAddressList() {
        List<InetAddress> list = new ArrayList<>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                //System.out.println(netInterface.getName());
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        //System.out.println("本机的IP = " + ip.getHostAddress());
                        list.add(ip);
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return list;
    }

    /**
     * 将value二进制表示的index位，置为0
     *
     * @param value
     * @param index 从右至左，以0为起始
     * @return 转换后的整数
     */
    public final static int bitsToZeroAtIndex(int value, int index) {
        return value ^ (1 << index);
    }

    /**
     * 将value二进制表示的index位，置为1
     *
     * @param value
     * @param index 从右至左，以0为起始
     * @return 转换后的整数
     */
    public final static int bitsToOneAtIndex(int value, int index) {
        return value | (1 << index);
    }

    /**
     * 将两个整形数据转码为一个长整形数据
     *
     * @param value1 将int的四个字节保存在前四位
     * @param value2 将int的四个字节保存在后四位
     * @return 转码后的长整形数据
     */
    public final static long merge(int value1, int value2) {
        return (((long) value1) << 32) + value2;
    }

    /**
     * 按字节分割长整形数据为两个整形数据
     *
     * @param value
     * @return
     */
    public final static int[] splite(long value) {
        return new int[]{(int) (value >> 32), (int) ((value << 32) >> 32)};
    }

    /**
     * 判断身份证上的年龄是否达到ageYear年龄
     *
     * @param idcard  身份证号码
     * @param ageYear 周岁
     * @return 满足为true
     */
    public static boolean fullYearOfLife(String idcard, int ageYear) {
        if (idcard == null || "".equals(idcard)) {
            return false;
        }
        String birthdayStr = idcard.substring(6, 14);
        int year = Integer.parseInt(birthdayStr.substring(0, 4));
        int month = Integer.parseInt(birthdayStr.substring(4, 6));
        int day = Integer.parseInt(birthdayStr.substring(6));
        LocalDate birthdayDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        int intervalYear = (int) ChronoUnit.YEARS.between(birthdayDate, currentDate);
        return intervalYear >= 18;
    }

    //IP正则
    private final static String ipRegex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)($|(?!\\.$)\\.)){4}$";
    //private final static String ipRegex = "((25[0-5]|2[0-4]//d|1//d{2}|[1-9]//d|//d)//.){3}(25[0-5]|2[0-4]//d|1//d{2}|[1-9]//d|//d)";

    /**
     * 判断是否为IP地址
     *
     * @param addr ip地址表示的字符串
     * @return true为ip地址
     */
    public final static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        Pattern pat = Pattern.compile(ipRegex);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    /**
     * 判断ip是否在某网段内
     *
     * @param ip 需判断的IP
     * @param startIp 起始网段IP
     * @param endIp 结束网段IP
     * @return true在网段内，否在不在网段内
     */
    public final static boolean ipIsInNetworkSegment(String ip, String startIp, String endIp) {
        if (ip == null) {
            throw new NullPointerException("IP不能为空！");
        }
        if (startIp == null) {
            throw new NullPointerException("IP段不能为空！");
        }
        if (endIp == null) {
            throw new NullPointerException("IP段不能为空！");
        }
        ip = ip.trim();
        if (!isIP(ip)) {
            return false;
        }
        startIp = startIp.trim();
        if (!isIP(startIp)) {
            return false;
        }
        endIp = endIp.trim();
        if (!isIP(endIp)) {
            return false;
        }
        String[] sips = startIp.split("\\.");
        String[] sipe = endIp.split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 判断ip是否在某网段内
     *
     * @param ip 需判断的IP
     * @param ipSegment IP网段，以“-”或“,”或“:”连接的两个IP地址
     * @return true在网段内，否在不在网段内
     */
    public final static boolean ipIsInNetworkSegment(String ip, String ipSegment) {
        if (ipSegment == null) {
            throw new NullPointerException("IP段不能为空！");
        }
        if (ipSegment.equals("")) {
            throw new NullPointerException("IP段不能为空！");
        }
        String[] ips = ipSegment.split("-|,|;");
        return ipIsInNetworkSegment(ip, ips[0], ips[1]);
    }

    /**
     * IPv4转数字
     *
     * @param ip IPv4地址
     * @return IPv4地址的数字表示
     */
    public final static long ipv4ToNumber(String ip) {
        boolean bool = isIP(ip);
        if (!bool) {
            throw new Error(ip + " 无效的IP地址");
        }
        String[] parts = ip.split("\\.");
        long sip1 = Long.parseLong(parts[0]);
        long sip2 = Long.parseLong(parts[1]);
        long sip3 = Long.parseLong(parts[2]);
        long sip4 = Long.parseLong(parts[3]);
        long result = sip1 << 24;
        result += sip2 << 16;
        result += sip3 << 8;
        result += sip4;
        return result;
    }

    /**
     * 数字转IPv4地址
     *
     * @param ip IPv4地址的数字表示
     * @return IPv4地址
     */
    public final static String numberToIpv4(long ip) {
        long ip1 = ip & 0xFF000000;
        ip1 = ip1 >> 24;
        long ip2 = ip & 0x00FF0000;
        ip2 = ip2 >> 16;
        long ip3 = ip & 0x0000FF00;
        ip3 = ip3 >> 8;
        long ip4 = ip & 0x000000FF;
        StringBuilder sb = new StringBuilder();
        sb.append(ip1).append('.');
        sb.append(ip2).append('.');
        sb.append(ip3).append('.');
        sb.append(ip4);
        return sb.toString();
    }

    /**
     * 判断字符串是否为url地址
     *
     * @param url 字符串
     * @return true表示字符串参数为url地址
     */
    public final static boolean isURL(String url) {
        if (url == null) {
            return false;
        }
        url = url.trim();
        if (url.equals("")) {
            return false;
        }
        String strRegex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*\"().&=+$%-]+: )?[0-9a-z_!~*\"().&=+$%-]+@)?"//ftp的user@
                + "(([0-9]{1,3}.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*\"()-]+.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*\"().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher m = pattern.matcher(url);
        return m.find();
    }

    private Tool() {
    }
}
