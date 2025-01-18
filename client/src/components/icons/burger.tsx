import { chakra } from '@chakra-ui/react';
import { FC } from 'react';

type BurgerIconProps = {
  width?: number;
  height?: number;
  fill?: string;
  onClick?: () => void;
};

export const BurgerIcon: FC<BurgerIconProps> = ({
  width,
  height,
  fill,
  onClick,
}) => {
  return (
    <chakra.svg
      width={width ? `${width}px` : '30px'}
      height={height ? `${height}px` : '30px'}
      viewBox="0 0 12 12"
      onClick={onClick}
      cursor="pointer"
    >
      <g>
        <rect
          fill={fill ?? 'currentColor'}
          height="1"
          width="10"
          x="0.5"
          y="5.5"
        />
        <rect
          fill={fill ?? 'currentColor'}
          height="1"
          width="10"
          x="0.5"
          y="2.5"
        />
        <rect
          fill={fill ?? 'currentColor'}
          height="1"
          width="10"
          x="0.5"
          y="8.5"
        />
      </g>
    </chakra.svg>
  );
};
