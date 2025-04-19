import { pollingPool } from "../../../../api/knowledge/knowledge";

// 关闭这个知识库id外别的知识库的轮询
export function closePollExcept(keepKnowledgeId){
    const keys = [...pollingPool.keys()]
    for(let i=0;i<keys.length;++i){
        // 不是需要保留的知识库id
        if(keys[i] !== keepKnowledgeId){
            const intervalID = pollingPool.get(keys[i])
            console.log(`关闭了${keys[i]}的轮询`)
            clearInterval(intervalID)
            pollingPool.delete(keys[i])
        }
    }
}

// 关闭这个知识库id的轮询
export function closePollById(knowledgeId){
    // console.log([...pollingPool.keys()])
    // console.log(knowledgeId)
    if(pollingPool.has(knowledgeId)){
        const intervalID = pollingPool.get(knowledgeId)
        clearInterval(intervalID)
        console.log('关闭了')
        pollingPool.delete(knowledgeId)
    }
}