import Vue from 'vue';
import VueResource from 'vue-resource';
import VueRouter from 'vue-router';
import VueMaterial from 'vue-material';
import 'vue-material/dist/vue-material.css';

import moment from 'moment'
moment.locale('ko');

//component register
import PageContent from './components/PageContent.vue';
import Keyword from './components/Keyword.vue';
import SentenceList from './components/SentenceList.vue';
import SentenceForm from './components/SentenceForm.vue';
import WordForm from './components/WordForm.vue';
import IndexSelect from './components/IndexSelect.vue';
import JobExecutor from './components/JobExecutor.vue';
import CodeBlock from './components/CodeBlock.vue';
import Simplert from 'vue2-simplert';
import VueStomp from "vue-stomp";
import VueClipboard from 'vue-clipboard2';


Vue.use(VueResource);
Vue.use(VueRouter);
Vue.use(VueMaterial);
Vue.use(VueStomp);
Vue.use(VueClipboard);

Vue.component('keyword', Keyword);
Vue.component('page-content', PageContent);
Vue.component('sentence-list', SentenceList);
Vue.component('sentence-form', SentenceForm);
Vue.component('word-form', WordForm);
Vue.component('simplert', Simplert);
Vue.component('index-select', IndexSelect);
Vue.component('job-executor', JobExecutor);
Vue.component('code-block', CodeBlock);

// 어디에 설정할까..?
const tagName = {
  'NNG': { 'category': '체언', 'desc': '일반 명사' },
  'NNP': { 'category': '체언', 'desc': '고유 명사' },
  'NNB': { 'category': '체언', 'desc': '의존 명사' },
  'NR': { 'category': '체언', 'desc': '수사' },
  'NP': { 'category': '체언', 'desc': '대명사' },
  'VV': { 'category': '용언', 'desc': '동사' },
  'VA': { 'category': '용언', 'desc': '형용사' },
  'VX': { 'category': '용언', 'desc': '보조 용언' },
  'VCP': { 'category': '용언', 'desc': '긍정 지정사' },
  'VCN': { 'category': '용언', 'desc': '부정 지정사' },
  'MM': { 'category': '관형사', 'desc': '관형사' },
  'MAG': { 'category': '부사', 'desc': '일반 부사' },
  'MAJ': { 'category': '부사', 'desc': '접속 부사' },
  'IC': { 'category': '감탄사', 'desc': '감탄사' },
  'JKS': { 'category': '조사', 'desc': '주격 조사' },
  'JKC': { 'category': '조사', 'desc': '보격 조사' },
  'JKG': { 'category': '조사', 'desc': '관형격 조사' },
  'JKO': { 'category': '조사', 'desc': '목적격 조사' },
  'JKB': { 'category': '조사', 'desc': '부사격 조사' },
  'JKV': { 'category': '조사', 'desc': '호격 조사' },
  'JKQ': { 'category': '조사', 'desc': '인용격 조사' },
  'JX': { 'category': '조사', 'desc': '보조사' },
  'JC': { 'category': '조사', 'desc': '접속 조사' },
  'EP': { 'category': '선어말 어미', 'desc': '선어말 어미' },
  'EF': { 'category': '어말 어미', 'desc': '종결 어미' },
  'EC': { 'category': '어말 어미', 'desc': '연결 어미' },
  'ETN': { 'category': '어말 어미', 'desc': '명사형 전성 어미' },
  'ETM': { 'category': '어말 어미', 'desc': '관형형 전성 어미' },
  'XPN': { 'category': '접두사', 'desc': '체언 접두사' },
  'XSN': { 'category': '접미사', 'desc': '명사 파생 접미사' },
  'XSV': { 'category': '접미사', 'desc': '동사 파생 접미사' },
  'XSA': { 'category': '접미사', 'desc': '형용사 파생 접미사' },
  'XSB': { 'category': '접미사', 'desc': '부사 파생 접미사' },
  'XR': { 'category': '어근', 'desc': '어근' },
  'SF': { 'category': '부호', 'desc': '마침표물음표,느낌표' },
  'SP': { 'category': '부호', 'desc': '쉼표,가운뎃점,콜론,빗금' },
  'SS': { 'category': '부호', 'desc': '따옴표,괄호표,줄표' },
  'SE': { 'category': '부호', 'desc': '줄임표' },
  'SO': { 'category': '부호', 'desc': '붙임표(물결,숨김,빠짐)' },
  'SW': { 'category': '부호', 'desc': '기타기호 (논리수학기호,화폐기호)' },
  'NF': { 'category': '분석 불능', 'desc': '명사추정범주' },
  'NV': { 'category': '분석 불능', 'desc': '용언추정범주' },
  'NA': { 'category': '분석 불능', 'desc': '분석불능범주' },
  'SL': { 'category': '한글 이외', 'desc': '외국어' },
  'SH': { 'category': '한글 이외', 'desc': '한자' },
  'SN': { 'category': '한글 이외', 'desc': '숫자' }
};


Vue.filter('formatBytes', function(bytes,decimals) {
  if(isNaN(bytes)) return '0 Bytes';
	if(bytes === 0) return '0 Bytes';

	let kilobyte = 1024;
	let dm = decimals || 2;
	let	sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
	let	i = Math.floor(Math.log(bytes) / Math.log(kilobyte));

	return parseFloat((bytes / Math.pow(kilobyte, i)).toFixed(dm)) + ' ' + sizes[i];
});

Vue.filter('formatScore', function(number) {
  return isNaN(number) ? 0 : parseFloat(number.toFixed(5))
});

Vue.filter('formatDate', function(value) {
  if (value) {
    return moment(value).format('YYYY-MM-DD HH:mm')
  }
});

Vue.filter('formatDuration', function(value) {
  if (value) {
    return moment.utc(value).format("HH:mm:ss");
  }
});

Vue.filter('tagName', function(tag, detail) {

  let info = tagName[tag];

  if(!info){
    return tag;
  }

  let name = info.desc;

  if(detail){
    name = info.category + ' (' + info.desc + ')'
  }

  return name
});

const concat = (x,y) => x.concat(y);

const flatMap = (f,xs) => xs.map(f).reduce(concat, []);

Array.prototype.flatMap = function(f) {
  return flatMap(f,this)
};

Vue.material.registerTheme({
  default: {
    primary: 'blue',
    accent: 'pink'
  },
  blue: {
    primary: 'blue',
    accent: 'pink'
  },
  indigo: {
    primary: 'indigo',
    accent: 'pink'
  },
  brown: {
    primary: 'brown',
    accent: 'green'
  },
  purple: {
    primary: 'purple',
    accent: 'blue'
  },
  orange: {
    primary: 'orange',
    accent: 'purple'
  },
  green: {
    primary: 'green',
    accent: 'pink'
  },
  'light-blue': {
    primary: 'light-blue',
    accent: 'yellow'
  },
  teal: {
    primary: 'teal',
    accent: 'orange'
  },
  'blue-grey': {
    primary: 'blue-grey',
    accent: 'blue'
  },
  cyan: {
    primary: 'cyan',
    accent: 'pink'
  },
  red: {
    primary: 'red',
    accent: 'pink'
  },
  white: {
    primary: 'white',
    accent: 'blue'
  },
  grey: {
    primary: {
      color: 'grey',
      hue: 300
    },
    accent: 'indigo'
  }
});

