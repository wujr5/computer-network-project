package Protocol;

public enum ProtocolType {
  REQUEST_ONLINE_USER_LIST, // ���������û���Ϣ
  REQUEST_GROUP_LIST, // ����Ⱥ����Ϣ
  REQUEST_ONLINE_OR_OFFLINE, // ������������
  REQUEST_SHARE_SCREEN, // ������Ļ����
  
  INFO_SHARE_SCREEN_SHUTDOWN, // �ر���Ļ��������
  
  SENDTO_UNREADED_CHATTING_ITEM, // ����δ����Ϣ
  SENDTO_PERSONAL_CHATTING_ITEM, // ���͸�����Ϣ��P2Pͨ��
  SENDTO_REGISTER, // ע������
  SENDTO_LOGIN, // ��¼����
  SENDTO_LOGOUT, // ��������
  SENDTO_NEW_GROUP_CHATTING, // �½�Ⱥ��
  
  RESPONSE_REGISTER, // ע����Ӧ
  RESPONSE_LOGIN, // ��¼��Ӧ
  RESPONSE_ONLINE_USER_LIST, // �����û��б���Ӧ
  RESPONSE_SHARE_SCREEN,
  
  BROADCAST_USER_ONLINE_OR_OFFLIE, // �㲥��Ϣ���û����߻�������
  BROADCAST_NEW_GROUP,
  BROADCAST_GROUP_CHATTING_RECORD // �㲥��Ϣ��Ⱥ��
}
