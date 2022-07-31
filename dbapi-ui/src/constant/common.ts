import { IDataType,IContentType,IPrevilege,OptionItem } from '/@/type/constant/common'
const DATA_TYPE:IDataType = {
  STRING: 'string',
  BIGINT: 'bigint',
  DOUBLE: 'double',
  DATE: 'date',
  ARRAY_STRING: 'Array<string>',
  ARRAY_BIGINT: 'Array<bigint>',
  ARRAY_DOUBLE: 'Array<double>',
  ARRAY_DATE: 'Array<date>'
}

const DATA_TYPE_OPTIONS:OptionItem<string>[] = [
  { label: 'string', value: DATA_TYPE.STRING },
  { label: 'bigint', value: DATA_TYPE.BIGINT },
  { label: 'double', value: DATA_TYPE.DOUBLE },
  { label: 'date', value: DATA_TYPE.DATE },
  { label: 'string 数组', value: DATA_TYPE.ARRAY_STRING },
  { label: 'bigint 数组', value: DATA_TYPE.ARRAY_BIGINT },
  { label: 'double 数组', value: DATA_TYPE.ARRAY_DOUBLE },
  { label: 'date 数组', value: DATA_TYPE.ARRAY_DATE }
]

const CONTENT_TYPE:IContentType = {
  FORM_URLENCODED: 'application/x-www-form-urlencoded',
  JSON: 'application/json'
}

const PREVILEGE:IPrevilege = {
  PRIVATE: 0,
  PUBLIC: 1
}

export {
  DATA_TYPE,
  CONTENT_TYPE,
  PREVILEGE,
  DATA_TYPE_OPTIONS
}