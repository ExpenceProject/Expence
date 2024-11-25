import { chakra } from '@chakra-ui/react';
import { FC } from 'react';

import illustration from '../../../assets/images/error_not_found.svg';

type ErrorNotFoundProps = {
  width?: number;
  height?: number;
};

const ErrorNotFound: FC<ErrorNotFoundProps> = ({ width, height }) => {
  return (
    <chakra.img
      src={illustration}
      alt="Error Not Found"
      width={width ? `${width}px` : '500px'}
      height={height ? `${height}px` : 'auto'}
    />
  );
};

export default ErrorNotFound;
