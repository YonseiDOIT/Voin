const CommentResult = ({ comment }: { comment?: string }) => {
    return (
        <div className="bg-white w-full p-6 rounded-[32px] shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]">
            <div className="w-full pr-2">
                <span className="line-15 text-[15px] font-medium text-grey-40 break-words whitespace-pre-line">
                    {comment ? comment : '작성된 내용이 없습니다.'}
                </span>
            </div>
        </div>
    )
}

export default CommentResult;