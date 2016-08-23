SHaRED doesn't directly call the ANDS DOI Service. Instead it calls the TERN specific services that in turn (probably) calls ANDS.

You can find the code for the TERN service here: https://github.com/ternoffice

Some examples of the calls that can be made are here: https://doi.tern.uq.edu.au/test/doi-test.html

At the time of writing, it seems that the TERN service is still using v2.1 of the schema which you can probably check here: https://github.com/ternoffice/tern-doi-prod/blob/master/protected/models/_base/BaseDoc.php#L29

Ben added v2.2 of the schema to this repo but we haven't used it yet presumably because the TERN service doesn't support it as he mentions in commit b90b40f6c57ed744f91ebf0a102d1ac03800db46