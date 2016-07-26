# coding:utf8
from facepp import API
from facepp import File
from json import *
import string

class Myfacepp:
    # Face++密钥
    __API_KEY = '1404fbd3d6ebfa49d74bac01e1cc242e'
    __API_SECRET = 'LU70vnCbAN9J_-vIPQxi0EnXUES6KmJH'
    __api = None

    def __init__(self):
        self.__api = API(key=self.__API_KEY, secret=self.__API_SECRET)

    def do_recognize(self, img_path):
        try:
            detect_rst = self.__api.detection.detect(img=File(img_path))  # 进行图片检测返回的结果
            detect_rst = JSONEncoder().encode(detect_rst)  # 将从服务器获取的Json转换为dict并从中获取face_id值
            detect_rst = string.replace(detect_rst, "]", "")
            detect_rst = string.replace(detect_rst, "[", "")
            detect_rst = JSONDecoder().decode(detect_rst)
            faceid = detect_rst["face"]["face_id"]
            recognition_rst = self.__api.recognition.verify(person_name="PokerZues",
                                                            face_id=faceid)  # 通过face++判定是否为host并返回
            result = recognition_rst["is_same_person"]
            if result == True:
                return True
            else:
                return False
        except Exception, e:
            print(e)
