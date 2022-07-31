export interface IDataType {
  STRING: string,
  BIGINT: string,
  DOUBLE: string,
  DATE: string,
  ARRAY_STRING: string,
  ARRAY_BIGINT: string,
  ARRAY_DOUBLE: string,
  ARRAY_DATE: string
}

export interface IContentType {
  [index:string]:string
}
  
export interface IPrevilege {
  PRIVATE: number,
  PUBLIC: number
}

export interface OptionItem<T> {
  value: T
  label: string
}