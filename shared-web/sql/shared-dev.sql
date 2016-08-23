CREATE TABLE answer (
    id bigint NOT NULL,
    questionid character varying(255),
    response text,
    responsetype character varying(255),
    suggestedresponse character varying(255),
    image_id bigint,
    submission_id bigint,
    multianswer_id bigint,
    multiorder integer
);

ALTER TABLE public.answer OWNER TO "shared-dev";

CREATE TABLE answer_image (
    id bigint NOT NULL,
    date timestamp without time zone,
    description character varying(255),
    image bytea,
    imagename character varying(255),
    imageobjectid character varying(255),
    imagethumbnail bytea,
    imagethumbnailid character varying(255),
    imagetype character varying(255)
);


ALTER TABLE public.answer_image OWNER TO "shared-dev";

CREATE TABLE answer_review (
    id bigint NOT NULL,
    answerreviewoutcome character varying(255),
    comment character varying(255),
    editedentry character varying(255),
    questionid character varying(255),
    review_id bigint
);

ALTER TABLE public.answer_review OWNER TO "shared-dev";

CREATE TABLE email_notifications (
    id bigint NOT NULL,
    exceptionoccured boolean,
    message text,
    notificationtype character varying(255),
    sentdate timestamp without time zone,
    subject character varying(255),
    toaddress character varying(255),
    username character varying(255)
);


ALTER TABLE public.email_notifications OWNER TO "shared-dev";

CREATE TABLE file_review (
    id bigint NOT NULL,
    comments character varying(255),
    reviewoutcome character varying(255),
    review_id bigint,
    submission_data_id bigint
);

ALTER TABLE public.file_review OWNER TO "shared-dev";

CREATE SEQUENCE hibernate_sequence
    START WITH 4
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.hibernate_sequence OWNER TO postgres;

CREATE TABLE hist_submission (
    id bigint NOT NULL,
    commentsforreviewer character varying(255),
    historycreatedate timestamp without time zone,
    lastreviewdate timestamp without time zone,
    status character varying(255),
    submissiondate timestamp without time zone,
    config_id bigint,
    submission_id bigint NOT NULL,
    review_id bigint,
    user_id character varying(255) NOT NULL
);

ALTER TABLE public.hist_submission OWNER TO "shared-dev";

CREATE TABLE hist_submission_answer (
    hist_submission_id bigint NOT NULL,
    answers_id bigint NOT NULL
);


ALTER TABLE public.hist_submission_answer OWNER TO "shared-dev";

CREATE TABLE hist_submission_submission_data (
    hist_submission_id bigint NOT NULL,
    submissiondatalist_id bigint NOT NULL
);

ALTER TABLE public.hist_submission_submission_data OWNER TO "shared-dev";

CREATE TABLE question (
    id bigint NOT NULL,
    questionid character varying(255),
    questiontext character varying(255),
    responsetype character varying(255),
    traitname character varying(255),
    config_id bigint NOT NULL
);

ALTER TABLE public.question OWNER TO "shared-dev";

CREATE TABLE questionnaire_config (
    id bigint NOT NULL,
    active boolean,
    configfilename character varying(255),
    importdate timestamp without time zone,
    version character varying(255),
    xml text
);

ALTER TABLE public.questionnaire_config OWNER TO "shared-dev";

CREATE TABLE shared_authorities (
    id bigint NOT NULL,
    authority character varying(255),
    username character varying(255)
);

ALTER TABLE public.shared_authorities OWNER TO "shared-dev";

CREATE TABLE shared_user (
    username character varying(255) NOT NULL,
    emailaddress character varying(255),
    enabled boolean,
    password character varying(255),
    registrationtoken character varying(255)
);


ALTER TABLE public.shared_user OWNER TO "shared-dev";

--
-- TOC entry 176 (class 1259 OID 91421)
-- Dependencies: 5
-- Name: shared_user_shared_authorities; Type: TABLE; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE TABLE shared_user_shared_authorities (
    shared_user_username character varying(255) NOT NULL,
    roles_id bigint NOT NULL
);


ALTER TABLE public.shared_user_shared_authorities OWNER TO "shared-dev";

--
-- TOC entry 177 (class 1259 OID 91428)
-- Dependencies: 5
-- Name: storage_location; Type: TABLE; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE TABLE storage_location (
    type character varying(31) NOT NULL,
    id bigint NOT NULL,
    obj_name character varying(255),
    fspath character varying(255),
    objectid character varying(255),
    objectstoreidentifier character varying(255),
    data_id bigint NOT NULL
);


ALTER TABLE public.storage_location OWNER TO "shared-dev";

--
-- TOC entry 178 (class 1259 OID 91436)
-- Dependencies: 5
-- Name: submission; Type: TABLE; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE TABLE submission (
    id bigint NOT NULL,
    commentsforreviewer character varying(255),
    lastreviewdate timestamp without time zone,
    status character varying(255),
    submissiondate timestamp without time zone,
    title character varying(255),
    config_id bigint,
    user_id character varying(255) NOT NULL
);


ALTER TABLE public.submission OWNER TO "shared-dev";

--
-- TOC entry 179 (class 1259 OID 91444)
-- Dependencies: 5
-- Name: submission_data; Type: TABLE; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE TABLE submission_data (
    id bigint NOT NULL,
    embargodate timestamp without time zone,
    filedescription character varying(1000),
    filename character varying(255),
    filesizebytes bigint,
    sitefile_cs_other character varying(255),
    sitefile_cs character varying(255),
    submissiondatatype character varying(255),
    submission_id bigint
);


ALTER TABLE public.submission_data OWNER TO "shared-dev";

--
-- TOC entry 161 (class 1259 OID 16445)
-- Dependencies: 5
-- Name: submission_data_seq; Type: SEQUENCE; Schema: public; Owner: shared-dev
--

CREATE SEQUENCE submission_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.submission_data_seq OWNER TO "shared-dev";

--
-- TOC entry 180 (class 1259 OID 91452)
-- Dependencies: 5
-- Name: submission_review; Type: TABLE; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE TABLE submission_review (
    id bigint NOT NULL,
    notes character varying(255),
    rejectionmessage character varying(255),
    reviewdate timestamp without time zone,
    reviewoutcome character varying(255),
    reviewer_id character varying(255) NOT NULL,
    submission_id bigint
);


ALTER TABLE public.submission_review OWNER TO "shared-dev";

--
-- TOC entry 2207 (class 2606 OID 91350)
-- Dependencies: 165 165 2263
-- Name: answer_image_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY answer_image
    ADD CONSTRAINT answer_image_pkey PRIMARY KEY (id);


--
-- TOC entry 2205 (class 2606 OID 91342)
-- Dependencies: 164 164 2263
-- Name: answer_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY answer
    ADD CONSTRAINT answer_pkey PRIMARY KEY (id);


--
-- TOC entry 2209 (class 2606 OID 91358)
-- Dependencies: 166 166 2263
-- Name: answer_review_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY answer_review
    ADD CONSTRAINT answer_review_pkey PRIMARY KEY (id);


--
-- TOC entry 2211 (class 2606 OID 91366)
-- Dependencies: 167 167 2263
-- Name: email_notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY email_notifications
    ADD CONSTRAINT email_notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 2213 (class 2606 OID 91374)
-- Dependencies: 168 168 2263
-- Name: file_review_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY file_review
    ADD CONSTRAINT file_review_pkey PRIMARY KEY (id);


--
-- TOC entry 2215 (class 2606 OID 91382)
-- Dependencies: 169 169 2263
-- Name: hist_submission_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY hist_submission
    ADD CONSTRAINT hist_submission_pkey PRIMARY KEY (id);


--
-- TOC entry 2217 (class 2606 OID 91396)
-- Dependencies: 172 172 2263
-- Name: question_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY question
    ADD CONSTRAINT question_pkey PRIMARY KEY (id);


--
-- TOC entry 2219 (class 2606 OID 91404)
-- Dependencies: 173 173 2263
-- Name: questionnaire_config_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY questionnaire_config
    ADD CONSTRAINT questionnaire_config_pkey PRIMARY KEY (id);


--
-- TOC entry 2221 (class 2606 OID 91412)
-- Dependencies: 174 174 2263
-- Name: shared_authorities_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY shared_authorities
    ADD CONSTRAINT shared_authorities_pkey PRIMARY KEY (id);


--
-- TOC entry 2224 (class 2606 OID 91420)
-- Dependencies: 175 175 2263
-- Name: shared_user_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY shared_user
    ADD CONSTRAINT shared_user_pkey PRIMARY KEY (username);


--
-- TOC entry 2226 (class 2606 OID 91425)
-- Dependencies: 176 176 176 2263
-- Name: shared_user_shared_authorities_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY shared_user_shared_authorities
    ADD CONSTRAINT shared_user_shared_authorities_pkey PRIMARY KEY (shared_user_username, roles_id);


--
-- TOC entry 2228 (class 2606 OID 91427)
-- Dependencies: 176 176 2263
-- Name: shared_user_shared_authorities_roles_id_key; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY shared_user_shared_authorities
    ADD CONSTRAINT shared_user_shared_authorities_roles_id_key UNIQUE (roles_id);


--
-- TOC entry 2230 (class 2606 OID 91435)
-- Dependencies: 177 177 2263
-- Name: storage_location_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY storage_location
    ADD CONSTRAINT storage_location_pkey PRIMARY KEY (id);


--
-- TOC entry 2234 (class 2606 OID 91451)
-- Dependencies: 179 179 2263
-- Name: submission_data_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY submission_data
    ADD CONSTRAINT submission_data_pkey PRIMARY KEY (id);


--
-- TOC entry 2232 (class 2606 OID 91443)
-- Dependencies: 178 178 2263
-- Name: submission_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT submission_pkey PRIMARY KEY (id);


--
-- TOC entry 2236 (class 2606 OID 91459)
-- Dependencies: 180 180 2263
-- Name: submission_review_pkey; Type: CONSTRAINT; Schema: public; Owner: shared-dev; Tablespace: 
--

ALTER TABLE ONLY submission_review
    ADD CONSTRAINT submission_review_pkey PRIMARY KEY (id);


--
-- TOC entry 2222 (class 1259 OID 91540)
-- Dependencies: 174 2263
-- Name: username_ix; Type: INDEX; Schema: public; Owner: shared-dev; Tablespace: 
--

CREATE INDEX username_ix ON shared_authorities USING btree (username);


--
-- TOC entry 2249 (class 2606 OID 91520)
-- Dependencies: 170 2204 164 2263
-- Name: answer_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission_answer
    ADD CONSTRAINT answer_fk FOREIGN KEY (answers_id) REFERENCES answer(id);


--
-- TOC entry 2252 (class 2606 OID 91535)
-- Dependencies: 172 2218 173 2263
-- Name: config_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY question
    ADD CONSTRAINT config_fk FOREIGN KEY (config_id) REFERENCES questionnaire_config(id);


--
-- TOC entry 2242 (class 2606 OID 91485)
-- Dependencies: 168 179 2233 2263
-- Name: file_review_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY file_review
    ADD CONSTRAINT file_review_fk FOREIGN KEY (submission_data_id) REFERENCES submission_data(id);


--
-- TOC entry 2254 (class 2606 OID 91546)
-- Dependencies: 176 2223 175 2263
-- Name: fk_authorities_users; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY shared_user_shared_authorities
    ADD CONSTRAINT fk_authorities_users FOREIGN KEY (shared_user_username) REFERENCES shared_user(username);


--
-- TOC entry 2255 (class 2606 OID 91551)
-- Dependencies: 174 2220 176 2263
-- Name: fkdce0bd414438813e; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY shared_user_shared_authorities
    ADD CONSTRAINT fkdce0bd414438813e FOREIGN KEY (roles_id) REFERENCES shared_authorities(id);


--
-- TOC entry 2241 (class 2606 OID 91480)
-- Dependencies: 175 167 2223 2263
-- Name: fke08d09451b50dc61; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY email_notifications
    ADD CONSTRAINT fke08d09451b50dc61 FOREIGN KEY (username) REFERENCES shared_user(username);


--
-- TOC entry 2248 (class 2606 OID 91515)
-- Dependencies: 2214 169 170 2263
-- Name: hist_submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission_answer
    ADD CONSTRAINT hist_submission_fk FOREIGN KEY (hist_submission_id) REFERENCES hist_submission(id);


--
-- TOC entry 2250 (class 2606 OID 91525)
-- Dependencies: 169 171 2214 2263
-- Name: hist_submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission_submission_data
    ADD CONSTRAINT hist_submission_fk FOREIGN KEY (hist_submission_id) REFERENCES hist_submission(id);


--
-- TOC entry 2238 (class 2606 OID 91465)
-- Dependencies: 2204 164 164 2263
-- Name: multianswer_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY answer
    ADD CONSTRAINT multianswer_fk FOREIGN KEY (multianswer_id) REFERENCES answer(id);


--
-- TOC entry 2244 (class 2606 OID 91495)
-- Dependencies: 169 173 2218 2263
-- Name: questionnaire_config_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission
    ADD CONSTRAINT questionnaire_config_fk FOREIGN KEY (config_id) REFERENCES questionnaire_config(id);


--
-- TOC entry 2258 (class 2606 OID 91561)
-- Dependencies: 178 2218 173 2263
-- Name: questionnaire_config_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT questionnaire_config_fk FOREIGN KEY (config_id) REFERENCES questionnaire_config(id);


--
-- TOC entry 2260 (class 2606 OID 91576)
-- Dependencies: 175 2223 180 2263
-- Name: shared_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY submission_review
    ADD CONSTRAINT shared_user_fk FOREIGN KEY (reviewer_id) REFERENCES shared_user(username);


--
-- TOC entry 2237 (class 2606 OID 91460)
-- Dependencies: 165 164 2206 2263
-- Name: submission_answer_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY answer
    ADD CONSTRAINT submission_answer_fk FOREIGN KEY (image_id) REFERENCES answer_image(id);


--
-- TOC entry 2251 (class 2606 OID 91530)
-- Dependencies: 2233 171 179 2263
-- Name: submission_data_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission_submission_data
    ADD CONSTRAINT submission_data_fk FOREIGN KEY (submissiondatalist_id) REFERENCES submission_data(id);


--
-- TOC entry 2256 (class 2606 OID 91556)
-- Dependencies: 179 2233 177 2263
-- Name: submission_data_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY storage_location
    ADD CONSTRAINT submission_data_fk FOREIGN KEY (data_id) REFERENCES submission_data(id);


--
-- TOC entry 2239 (class 2606 OID 91470)
-- Dependencies: 164 178 2231 2263
-- Name: submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY answer
    ADD CONSTRAINT submission_fk FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- TOC entry 2247 (class 2606 OID 91510)
-- Dependencies: 178 2231 169 2263
-- Name: submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission
    ADD CONSTRAINT submission_fk FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- TOC entry 2259 (class 2606 OID 91571)
-- Dependencies: 2231 178 179 2263
-- Name: submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY submission_data
    ADD CONSTRAINT submission_fk FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- TOC entry 2261 (class 2606 OID 91581)
-- Dependencies: 178 180 2231 2263
-- Name: submission_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY submission_review
    ADD CONSTRAINT submission_fk FOREIGN KEY (submission_id) REFERENCES submission(id);


--
-- TOC entry 2240 (class 2606 OID 91475)
-- Dependencies: 180 2235 166 2263
-- Name: submission_review_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY answer_review
    ADD CONSTRAINT submission_review_fk FOREIGN KEY (review_id) REFERENCES submission_review(id);


--
-- TOC entry 2243 (class 2606 OID 91490)
-- Dependencies: 2235 168 180 2263
-- Name: submission_review_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY file_review
    ADD CONSTRAINT submission_review_fk FOREIGN KEY (review_id) REFERENCES submission_review(id);


--
-- TOC entry 2246 (class 2606 OID 91505)
-- Dependencies: 169 2235 180 2263
-- Name: submission_review_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission
    ADD CONSTRAINT submission_review_fk FOREIGN KEY (review_id) REFERENCES submission_review(id);


--
-- TOC entry 2245 (class 2606 OID 91500)
-- Dependencies: 2223 175 169 2263
-- Name: submitter_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY hist_submission
    ADD CONSTRAINT submitter_user_fk FOREIGN KEY (user_id) REFERENCES shared_user(username);


--
-- TOC entry 2257 (class 2606 OID 91566)
-- Dependencies: 2223 175 178 2263
-- Name: submitter_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY submission
    ADD CONSTRAINT submitter_user_fk FOREIGN KEY (user_id) REFERENCES shared_user(username);


--
-- TOC entry 2253 (class 2606 OID 91541)
-- Dependencies: 174 2223 175 2263
-- Name: username_authority_fk; Type: FK CONSTRAINT; Schema: public; Owner: shared-dev
--

ALTER TABLE ONLY shared_authorities
    ADD CONSTRAINT username_authority_fk FOREIGN KEY (username) REFERENCES shared_user(username);


--
-- TOC entry 2268 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2270 (class 0 OID 0)
-- Dependencies: 181
-- Name: hibernate_sequence; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON SEQUENCE hibernate_sequence FROM PUBLIC;
REVOKE ALL ON SEQUENCE hibernate_sequence FROM postgres;
GRANT ALL ON SEQUENCE hibernate_sequence TO postgres;
GRANT ALL ON SEQUENCE hibernate_sequence TO "shared-dev";


-- Completed on 2013-02-07 16:53:51 CST

--
-- PostgreSQL database dump complete
--

