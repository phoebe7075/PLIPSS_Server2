package plibss.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;



public class Protocol {
    public static final int LEN_TYPE = 1;
    public static final int LEN_CODE = 1;
    public static final int LEN_BODYLENGTH = 2;
    public static final int LEN_HEADER = 4; // 4 Byte
    public static final int LEN_MAX = 50000; // 최대 데이터 길이

    public static final int TYPE_ERROR = -1; // 오류 응답
    public static final int TYPE_UNDEFINED = 0; // 프로토콜이 지정되어 있지 않은 경우
    public static final int TYPE_EXIT = 1; // 프로그램 종료
    public static final int TYPE_LOGIN_REQ = 2; // 로그인 요청
    public static final int TYPE_LOGIN_RES = 3; // 로그인 응답
    public static final int TYPE_LOGOUT_REQ = 4; // 로그아웃 요청
    public static final int TYPE_LOGOUT_RES = 5; // 로그인 응답
    public static final int TYPE_USER_INFO_REQ = 6; // 회원정보 요청 (단일, 자기자신)
    public static final int TYPE_USER_INFO_RES = 7; // 회원정보 응답 (단일, 자기자신)

    public static final int TYPE_LIBRARY_ENROLL_REQ = 8;  // 도서관 정보 등록 요청
    public static final int TYPE_LIBRARY_ENROLL_RES = 9;  // 도서관 정보 등록 응답
    public static final int TYPE_LABRARY_UPDATE_REQ = 10;  // 도서관 정보 수정 요청
    public static final int TYPE_LABRARY_UPDATE_RES = 11;  // 도서관 정보 수정 응답
    public static final int TYPE_LABRARY_DELETE_REQ = 12;  // 도서관 정보 삭제 요청
    public static final int TYPE_LABRARY_DELETE_RES = 13;  // 도서관 정보 삭제 응답
    public static final int TYPE_LABRARY_LIST_INFO_REQ = 14;  // 도서관 검색목록 조회 요청
    public static final int TYPE_LABRARY_LIST_INFO_RES = 15;  // 도서관 검색목록 조회 응답
    public static final int TYPE_LABRARY_DETAILS_INFO_REQ = 16;  // 도서관 세부정보 조회 요청
    public static final int TYPE_LABRARY_DETAILS_INFO_RES = 17;  // 도서관 세부정보 조회 응답

    public static final int TYPE_BOOK_ENROLL_REQ = 18; // 도서 정보 등록 요청
    public static final int TYPE_BOOK_ENROLL_RES = 19; // 도서 정보 등록 응답
    public static final int TYPE_BOOK_DELETE_REQ = 20; // 도서 정보 삭제 요청
    public static final int TYPE_BOOK_DELETE_RES = 21; // 도서 정보 삭제 응답
    public static final int TYPE_BOOK_UPDATE_REQ = 22; // 도서 정보 수정 요청
    public static final int TYPE_BOOK_UPDATE_RES = 23; // 도서 정보 수정 응답
    public static final int TYPE_BOOK_LIST_INFO_REQ = 24; // 도서 목록 조회 요청
    public static final int TYPE_BOOK_LIST_INFO_RES = 25; // 도서 목록 조회 응답
    public static final int TYPE_BOOK_DETAILS_INFO_REQ = 26; // 도서 세부정보 조회 요청
    public static final int TYPE_BOOK_DETALES_INFO_RES = 27; // 도서 세부정보 조회 응답

    public static final int TYPE_LABRARY_FAVORITEINFO_ENROLL_REQ = 28; // 도서관 즐겨찾기 정보 등록 요청
    public static final int TYPE_LABRARY_FAVORITEINFO_ENROLL_RES = 29; // 도서관 즐겨찾기 정보 등록 응답
    public static final int TYPE_LABRARY_FAVORITEINFO_DELETE_REQ = 30; // 도서관 즐겨찾기 정보 삭제 요청
    public static final int TYPE_LABRARY_FAVORITEINFO_DELETE_RES = 31; // 도서관 즐겨찾기 정보 삭제 응답
    public static final int TYPE_LABRARY_FAVORITEINFO_INFO_REQ = 32; // 도서관 즐겨찾기 정보 조회 요청
    public static final int TYPE_LABRARY_FAVORITEINFO_INFO_RES = 33; // 도서관 즐겨찾기 정보 조회 응답
    public static final int TYPE_BOOK_FAVORITEINFO_ENROLL_REQ = 34; // 도서 즐겨찾기 정보 등록 요청
    public static final int TYPE_BOOK_FAVORITEINFO_ENROLL_RES = 35; // 도서 즐겨찾기 정보 등록 응답
    public static final int TYPE_BOOK_FAVORITEINFO_DELETE_REQ = 36; // 도서 즐겨찾기 정보 삭제 요청
    public static final int TYPE_BOOK_FAVORITEINFO_DELETE_RES = 37; // 도서 즐겨찾기 정보 삭제 응답
    public static final int TYPE_BOOK_FAVORITEINFO_INFO_REQ = 38; // 도서 즐겨찾기 정보 조회 요청
    public static final int TYPE_BOOK_FAVORITEINFO_INFO_RES = 39; // 도서 즐겨찾기 정보 조회 응답

    public static final int TYPE_BOOK_BORROW_POSSIBILITY_REQ = 40; // 도서 대출가능 알림 요청
    public static final int TYPE_BOOK_BORROW_POSSIBILITY_RES = 41; // 도서 대출가능 알림 응답



    private byte type;
    private byte code;
    private short bodyLength;

    private byte[] body; // 직렬화 하여 저장

    public Protocol() {
        this(TYPE_UNDEFINED, 0);
    }

    public Protocol(int type) {
        this(type, 0);
    }

    public Protocol(int type, int code) {
        setType(type);
        setCode(code);
        setBodyLength(0);
    }


    public byte getType() {
        return type;
    }

    public void setType(int type) {
        this.type = (byte) type;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = (byte) code;
    }

    public short getBodyLength() {
        return bodyLength;
    }

    private void setBodyLength(int bodyLength) { // Body Length는 직접 설정할 수 없음
        this.bodyLength = (short) bodyLength;
    }

    public Object getBody() {
        return deserialize(body);
    }

    public void setBody(Object body) {
        byte[] serializedObject = serialize(body);
        this.body = serializedObject;
        setBodyLength(serializedObject.length);
    }

    public byte[] getPacket() { // 현재 header와 body로 패킷을 생성하여 리턴
        byte[] packet = new byte[LEN_HEADER + getBodyLength()];

        packet[0] = getType();
        packet[LEN_TYPE] = getCode();
        System.arraycopy(shortToByte(getBodyLength()), 0, packet, LEN_TYPE + LEN_CODE, LEN_BODYLENGTH);

        if (getBodyLength() > 0) {
            System.arraycopy(body, 0, packet, LEN_HEADER, getBodyLength());
        }

        return packet;
    }

    public void setPacketHeader(byte[] packet) { // 매개 변수 packet을 통해 header만 생성
        byte[] data;

        setType(packet[0]);
        setCode(packet[LEN_TYPE]);

        data = new byte[LEN_BODYLENGTH];
        System.arraycopy(packet, LEN_TYPE + LEN_CODE, data, 0, LEN_BODYLENGTH);
        setBodyLength(byteToShort(data));
    }

    public void setPacketBody(byte[] packet) { // 매개 변수 packet을 통해 body를 생성
        byte[] data;

        if (getBodyLength() > 0) {
            data = new byte[getBodyLength()];
            System.arraycopy(packet, 0, data, 0, getBodyLength());
            if (getType() == 121 || getType() == 124)
                setBody(data);
            else
                setBody(deserialize(data));
        }
    }

    private byte[] serialize(Object b) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(b);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object deserialize(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object ob = ois.readObject();
            return ob;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] shortToByte(short s) {
        return ByteBuffer.allocate(Short.SIZE / 8).putShort(s).array();
    }

    private int byteToShort(byte[] b) {
        return ByteBuffer.wrap(b).getShort();
    }
}

