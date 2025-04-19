export interface ChatDialogProps {
	id: number;
	// 1代表是AI回复，0是用户提问
	idClass: number;
	chatId: number;
	content: string;
	createTime: string;
	updateTime: string | null;
	chatSourceList: ChatSourceList[];
}

export interface ChatDialogPropsArray {
	propsArray: ChatDialogProps[];
}

export interface ChatSourceList {
	fileId: string;
	fileName: string;
	content: string;
	score: number;
}
