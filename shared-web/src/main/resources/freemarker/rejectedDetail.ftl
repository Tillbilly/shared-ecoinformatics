${emailText}

-------------------------------------------------------------------------------

Please click on this link to modify the submission:
${modifyUrl}


<#if (responseRejections?size > 0 ) >
The reviewer rejected the following submission responses:
<#list responseRejections as resp>

${resp.questionId} ${resp.question} 
RESPONSE: ${resp.response }
COMMENTS: ${resp.review }

</#list> 
</#if>
<#if ( dataRejections?size > 0 ) >

The reviewer rejected the following data items:
<#list dataRejections as dataRejection>

FILENAME: ${dataRejection.fileName}
DESCRIPTION: ${dataRejection.fileDescription}
COMMENTS: ${dataRejection.review}
  
</#list> 
</#if>