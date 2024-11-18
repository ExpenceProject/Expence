import { chakra } from '@chakra-ui/react';
import { FC } from 'react';

type CloseTriggerProps = {
  width?: number;
  height?: number;
  fill?: string;
  onClick?: () => void;
};

const CloseTrigger: FC<CloseTriggerProps> = ({
  width,
  height,
  fill,
  onClick,
}) => {
  return (
    <chakra.svg
      width={width ? `${width}px` : '20px'}
      height={height ? `${height}px` : '20px'}
      viewBox="0 0 512 512"
      onClick={onClick}
      fill={fill ?? 'currentColor'}
      cursor="pointer"
    >
      <g>
        <g>
          <polygon
            points="512,59.076 452.922,0 256,196.922 59.076,0 0,59.076 196.922,256 0,452.922 59.076,512 256,315.076 452.922,512 
			512,452.922 315.076,256 		"
          />
        </g>
      </g>
    </chakra.svg>
  );
};

export default CloseTrigger;
